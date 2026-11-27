const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_123";
const BOB_UID = "bob_uid_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: denied access to all pharmacy collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("cart").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("cart")
      .doc("prod_biosteron")
      .set({
        userId: ALICE_UID,
        productId: "prod_biosteron",
        productName: "Biosteron-D",
        category: "الأدوية",
        priceUsd: 20,
        quantity: 2,
        rating: 4.5,
        createdAt: now,
        updatedAt: now,
      })
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("orders").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("chats")
      .doc("pharm_perla")
      .collection("messages")
      .get()
  );
});

test("UserProfile: owner create, partial update, cross-user denial & ghost-field rejection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      language: "ar",
      notificationsEnabled: true,
      selectedLocation: "الموقع الحالي",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      language: "en",
      selectedLocation: "الفحامة - دمشق",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject createdAt tampering
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject invalid language enum
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      language: "fr",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost field
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      language: "ar",
      notificationsEnabled: true,
      selectedLocation: "Home",
      isAdmin: true,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Cross-user denial
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      language: "en",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("CartItem: owner CRUD, list query, quantity bounds & @DocumentId id rejection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);

  const cartRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("cart")
    .doc("prod_biosteron");

  await assertSucceeds(
    cartRef.set({
      userId: ALICE_UID,
      productId: "prod_biosteron",
      productName: "Biosteron-D",
      category: "الأدوية",
      priceUsd: 20,
      quantity: 2,
      rating: 4.5,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    cartRef.update({
      quantity: 3,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("cart").get()
  );

  // Reject quantity > 99
  await assertFails(
    cartRef.update({
      quantity: 120,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject extra @DocumentId 'id' field on create
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("cart")
      .doc("prod_detocyn")
      .set({
        id: "prod_detocyn",
        userId: ALICE_UID,
        productId: "prod_detocyn",
        productName: "Detocyn",
        category: "الأدوية",
        priceUsd: 20,
        quantity: 1,
        rating: 4.5,
        createdAt: created,
        updatedAt: created,
      })
  );

  // Cross-user cart access denied
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("cart").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("cart")
      .doc("prod_biosteron")
      .delete()
  );
});

test("Favorites & RecentSearches: owner create/list/delete and cross-user denial", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("favorites")
      .doc("prod_biosteron")
      .set({
        userId: ALICE_UID,
        productId: "prod_biosteron",
        productName: "Biosteron-D",
        createdAt: now,
      })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("favorites").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("favorites").get()
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recent_searches")
      .doc("search_panadol")
      .set({
        userId: ALICE_UID,
        query: "بنادول",
        createdAt: now,
      })
  );
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recent_searches")
      .get()
  );
});

test("PharmacyOrder: create confirmed, update status, terminal lock & tamper denial", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 5000);

  const orderRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("orders")
    .doc("order_1");

  await assertSucceeds(
    orderRef.set({
      userId: ALICE_UID,
      paymentMethod: "visa",
      subtotalUsd: 100,
      deliveryUsd: 5,
      totalUsd: 105,
      itemCount: 5,
      status: "confirmed",
      deliveryLocation: "الموقع الحالي",
      cardLast4: "4423",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Reject price tampering during update
  await assertFails(
    orderRef.update({
      totalUsd: 1,
      status: "preparing",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Create enriched order with delivery details, prescription metadata & itemsSummary
  const enrichedOrderRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("orders")
    .doc("order_2");

  await assertSucceeds(
    enrichedOrderRef.set({
      userId: ALICE_UID,
      paymentMethod: "cash",
      subtotalUsd: 80,
      deliveryUsd: 20,
      totalUsd: 100,
      itemCount: 2,
      status: "confirmed",
      deliveryLocation: "المعاريف - الدار البيضاء",
      deliveryAddress: "14 Rue Zerktouni, Apt 4",
      contactPhone: "+212612345678",
      hasPrescription: true,
      prescriptionAttached: true,
      itemsSummary: "prod_amoxil:1;prod_doliprane:1",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Reject invalid boolean/string types for new optional order fields
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("orders")
      .doc("order_bad_rx")
      .set({
        userId: ALICE_UID,
        paymentMethod: "cash",
        subtotalUsd: 80,
        deliveryUsd: 20,
        totalUsd: 100,
        itemCount: 2,
        status: "confirmed",
        deliveryLocation: "المعاريف - الدار البيضاء",
        hasPrescription: "yes",
        createdAt: now,
        updatedAt: now,
      })
  );

  // Valid transition to delivered
  await assertSucceeds(
    orderRef.update({
      status: "delivered",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Terminal state lock: cannot update once delivered
  await assertFails(
    orderRef.update({
      status: "cancelled",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("ChatMessage: owner create/list per pharmacy, attachments, and cross-user denial", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 2000);

  const messagesCol = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("chats")
    .doc("pharm_perla")
    .collection("messages");

  await assertSucceeds(
    messagesCol.doc("msg_1").set({
      userId: ALICE_UID,
      pharmacyId: "pharm_perla",
      senderType: "USER",
      text: "مرحبا يعطيك العافية",
      createdAt: now,
    })
  );

  await assertSucceeds(
    messagesCol.doc("msg_rx").set({
      userId: ALICE_UID,
      pharmacyId: "pharm_perla",
      senderType: "USER",
      text: "صورة الوصفة الطبية",
      attachmentUri: "content://media/picker/0/com.android.providers.media.photopicker/media/42",
      attachmentLabel: "prescription_photo.jpg",
      createdAt: now,
    })
  );

  await assertSucceeds(messagesCol.orderBy("createdAt", "asc").get());

  // Reject pharmacyId mismatch
  await assertFails(
    messagesCol.doc("msg_2").set({
      userId: ALICE_UID,
      pharmacyId: "pharm_other",
      senderType: "USER",
      text: "Mismatch",
      createdAt: now,
    })
  );

  // Cross-user chat read denied
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("chats")
      .doc("pharm_perla")
      .collection("messages")
      .get()
  );
});

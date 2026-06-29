<div align="center">

<img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
<img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
<img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />

# 📦 Expiry Tracker

**Never let anything expire unnoticed — track products, credentials, and subscriptions with shared access.**

</div>

***

## 🧐 What is Expiry Tracker?

**Expiry Tracker** is a modern Android app built with **Jetpack Compose** and **Firebase** to help you monitor the expiry dates of your physical products, digital subscriptions, and saved credentials — all in one place.

Whether it's a medicine cabinet, a set of domain subscriptions, or shared household consumables, Expiry Tracker keeps you informed with a clean Material 3 UI and real-time cloud sync.

***

## ✨ Features

| Feature | Description |
|---|---|
| 🏠 **Home Dashboard** | View all tracked items sorted by expiry date with status indicators |
| ➕ **Add Items** | Add products with name, purchase date, expiry date, amount (₹), and optional notes |
| 🔐 **Credential Storage** | Optionally attach a username, email, and password to each item — encrypted with AES-256 |
| 🗑️ **Expired Items** | Separate view for all expired and archived items |
| 📬 **Sharing & Invitations** | Share items with other users via email invite — invite system with accept/decline/revoke flow |
| 👤 **Profile** | Manage your account details |
| 🔐 **Authentication** | Email/password sign-up and login via Firebase Auth |
| 📱 **Edge-to-Edge UI** | Full Material 3 design with animated navigation transitions |
| 🔄 **Real-Time Sync** | Firestore-backed — changes reflect instantly across devices |

***

## 📂 Project Structure

```
app/src/main/java/com/dev/expirytracker/
│
├── MainActivity.kt              # Entry point — initializes Firebase & Compose UI
│
├── config/
│   └── AppConfig.kt             # Build-time config (collection names, encryption key)
│
├── model/
│   ├── ExpiryItem.kt            # Core data model (item name, dates, credentials, sharing)
│   ├── ShareInvitation.kt       # Invitation model (sender, receiver, status)
│   └── UserProfile.kt           # User profile model
│
├── service/
│   └── SharingService.kt        # Firestore sharing logic (invite, accept, revoke)
│
├── ui/
│   ├── MainContainer.kt         # Nav host, drawer, top bar, FAB, route definitions
│   ├── home/HomeScreen.kt       # Item list with expiry status chips
│   ├── add/AddItemScreen.kt     # Add new item form with date pickers & credential fields
│   ├── detail/DetailScreen.kt   # Full item detail + share management
│   ├── expired/ExpiredItemsScreen.kt  # Archive / expired items
│   ├── invitations/InvitationsScreen.kt  # Accept / decline shared invitations
│   ├── login/LoginScreen.kt     # Firebase Auth login
│   ├── login/RegisterScreen.kt  # New user registration
│   ├── profile/ProfileScreen.kt # User info & account settings
│   ├── about/AboutScreen.kt     # App info
│   └── theme/                   # Material 3 theming
│
└── util/
    └── CryptoManager.kt         # AES encryption/decryption for stored credentials
```

***

## 🏗️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Navigation Compose (`androidx.navigation:navigation-compose`)
- **Backend:** Firebase Firestore (real-time NoSQL database)
- **Auth:** Firebase Authentication (email/password)
- **Encryption:** AES-128 via `javax.crypto` + SHA-256 key derivation
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36
- **Build System:** Gradle (Kotlin DSL)

***

## 🔐 Security & Credentials

Each item optionally stores a `username`, `email`, and `password`. These fields are encrypted using **AES** (128-bit key derived from a SHA-256 hash of a secret key stored in `local.properties`) before being saved to Firestore, and decrypted on read.

> ⚠️ Credentials are stored per-item. Avoid storing highly sensitive credentials without understanding the implications of cloud storage.

***

## 🤝 Sharing System

Expiry Tracker has a full **invite-based sharing model**:

1. **Owner** sends an invite to another user's email from the Detail screen.
2. **Recipient** sees a pending invite in the **Invitations** screen.
3. They can **accept** (gaining read access to the item) or **decline**.
4. The owner can **revoke** access at any time.

Invitation statuses: `pending` → `accepted` / `declined` / `revoked`

***

## 🚀 Getting Started (Build from Source)

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11+
- A Firebase project with **Firestore** and **Authentication** enabled

### Setup

1. **Clone the repo:**
   ```bash
   git clone https://github.com/Mukesh-devs/ExpiryTracker.git
   cd ExpiryTracker
   ```

2. **Add Firebase config:**
    - Go to [Firebase Console](https://console.firebase.google.com) → Project Settings → Download `google-services.json`
    - Place it in `app/`

3. **Create `local.properties`** at the project root with:
   ```properties
   FIRESTORE_USERS_COLLECTION=users
   FIRESTORE_ITEMS_COLLECTION=items
   ENCRYPTION_SECRET_KEY=your_secret_key_here
   ```

4. **Enable Firestore Security Rules** (recommended):
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /items/{itemId} {
         allow read, write: if request.auth != null &&
           (resource.data.ownerId == request.auth.uid ||
            request.auth.uid in resource.data.sharedWith);
       }
     }
   }
   ```

5. **Build & Run:**
   ```bash
   ./gradlew assembleDebug
   ```
   Or open in Android Studio and press **Run ▶**.

***

## 📸 Screens Overview

| Screen | Description |
|---|---|
| **Login / Register** | Firebase Auth — email & password |
| **Home** | All active tracked items, sorted by expiry |
| **Add Item** | Form with date picker, notes, amount, optional credentials |
| **Detail** | Full item view with share management |
| **Expired Items** | Archive of past-expiry items |
| **Invitations** | Incoming share requests — accept or decline |
| **Profile** | Account info |
| **About** | App version & developer info |

***

Browse all releases: [github.com/Mukesh-devs/ExpiryTracker/releases](https://github.com/Mukesh-devs/ExpiryTracker/releases)

***

## 🛠️ Roadmap

- [ ] Push notifications for upcoming expiries
- [ ] Barcode scanning for quick product entry
- [ ] Category/tag filtering on the Home screen
- [ ] Export items to CSV
- [ ] Signed APK via GitHub Actions CI/CD

***

## 👨‍💻 Author

**Mukesh** — [@Mukesh-devs](https://github.com/Mukesh-devs)

***

## 📄 License

This project is open-source. See [LICENSE](LICENSE) for details.
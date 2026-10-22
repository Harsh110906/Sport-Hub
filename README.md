# 🏆 SportHub

**Smart Sports Tournament, Fitness, and Event Management System**

![SportHub Cover Image](https://via.placeholder.com/1200x400.png?text=SportHub+-+Smart+Sports+Management)

SportHub is a comprehensive, native Android application built to bridge the gap between sports organizers and athletes. Whether you are managing a local school football league, organizing a town marathon, or just tracking your daily workouts, SportHub provides a clean, fast, and unified platform to do it all.

---

## ✨ Key Features

SportHub utilizes a **Role-Based Access Control (RBAC)** architecture. Upon registration, users choose their role, dynamically altering their dashboard and permissions.

### 🛡️ For Admins (Organizers/Coaches)
*   **🏆 Tournament Management:** Create, update, and manage sports tournaments with custom capacities, venues, and date ranges.
*   **👥 Team Building:** Register teams, assign captains, and allocate them to specific tournaments.
*   **📅 Match Scheduling:** Schedule matches between teams, update live statuses (Scheduled, Ongoing, Completed), and input final scores.
*   **🎟️ Event Publishing:** Broadcast local sports events, clinics, or town-hall meetings to all registered players.
*   **💾 Data Export:** Export tournament, team, match, and event data locally to **CSV** format for external spreadsheet management.

### 🏃‍♂️ For Players (Athletes/Students)
*   **🔍 Browse & Join:** Explore upcoming tournaments and seamlessly register to participate. Track all joined leagues in a dedicated "My Registrations" hub.
*   **🗓️ Match Schedules:** View chronological match schedules with dynamic sorting (Earliest/Latest) and filtering by tournament and status.
*   **❤️ Fitness Tracker:** Log daily physical activities (Duration, Calories, Activity Type) and view an aggregated summary of your fitness journey.
*   **📋 Workout Planner:** Plan future routines, tag them by sport or exercise, and check them off as "Completed".
*   **🎫 Event Discovery:** Find local sports events happening in your area.

---

## 🎨 UI & Design System

SportHub was meticulously redesigned with a premium **Material Design 3** (M3) architecture. 
*   **Bento-Grid Dashboards:** Clean, rounded `MaterialCardView` grids with intuitive vector iconography.
*   **Dynamic Theming:** 100% support for **Light** and **Dark Modes**, mapped safely using semantic color attributes (`?attr/colorPrimary`, `?attr/colorSurface`).
*   **Status Chips:** Color-coded, rounded visual indicators for Match and Tournament statuses.
*   **User-Friendly Forms:** Extensive use of `TextInputLayout` with Outlined Boxes, `NestedScrollView` for accessibility on smaller devices, and robust form validation.

---

## 🛠️ Tech Stack & Architecture

SportHub is designed to be highly readable, structurally sound, and beginner-friendly, adhering to classic Android MVC patterns without the overhead of heavy third-party architectures.

*   **Language:** Java
*   **UI/Views:** XML (Android Standard View System), Material Components (`com.google.android.material:1.14.0`)
*   **Local Database:** SQLite (`SQLiteOpenHelper`) serving 8 relational tables (Users, Tournaments, Teams, Matches, Events, Fitness, Workouts, Registrations).
*   **Authentication:** Firebase Auth (Email/Password), fully decoupled from local SQLite profile data to prevent privilege escalation.
*   **State Management:** SharedPreferences (`SessionManager`)
*   **Testing:** JUnit & Espresso (Local and Instrumented UI Tests)

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio (Ladybug or newer recommended)
*   JDK 11+
*   Android SDK 24 (Min) to 36 (Target)

### Installation
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Harsh110906/Sport-Hub.git
    ```
2.  **Open the project** in Android Studio.
3.  **Firebase Configuration:**
    *   Create a project on the [Firebase Console](https://console.firebase.google.com/).
    *   Enable **Email/Password** Authentication.
    *   Download your `google-services.json` file and place it in the `app/` directory. *(Note: This file is intentionally git-ignored for security).*
4.  **Sync & Build:**
    *   Sync Gradle files.
    *   Run `AssembleDebug` or hit **Run** (`Shift + F10`) on your preferred emulator or physical device.

---

## 📁 Project Structure

```text
com.example.sporthub
├── activities/       # UI Controllers (Dashboards, Forms, Lists)
├── adapters/         # Custom ArrayAdapters for ListViews (In-memory filtering)
├── database/         # SQLiteOpenHelper (Schema definition and CRUD ops)
├── models/           # POJO Data classes (Match, Team, Tournament, etc.)
└── utils/            # SessionManager, Validators, and Helpers
```

---

## 🔒 Security Highlights

*   **Role Protection:** The `role` string is securely assigned at registration and never exposed to the Edit Profile UI. `SessionManager` and Activity `onCreate()` strictly bounce unauthorized access.
*   **Cursor Safety:** All SQLite `Cursor` instances are safely guarded within `try-finally` blocks to prevent memory leaks.
*   **Foreign Key Integrity:** SQLite Foreign Key Constraints are enabled by default. Deleting a parent entity (e.g., a Tournament) automatically and safely cascades deletion to its child elements (Teams, Matches, Registrations) to prevent orphaned data.

---

*Built for Android Mobile Application Development.*
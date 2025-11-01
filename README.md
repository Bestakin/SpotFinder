# SpotFinder

**SpotFinder** is a modern Android application that is built using **Kotlin** and **Jetpack Compose**, assisting the user in searching, viewing, and saving real-world locations.
It integrates the **Google Maps SDK**, **Places API (New)**, and **Geocoding API** to provide accurate and interactive location experiences, with offline persistence by using **Room Database**.

---

##  Features

- **Search Places:** Find any location worldwide using Google’s Places API.  
- **Map Integration:** View locations directly on Google Maps with dynamic markers.  
- **Save Locations:** Store favorite places locally using Room Database.  
- **Delete Saved Locations:** Remove any location from your saved list anytime.  
- **API Security:** API keys restricted via package name and SHA-1 fingerprint.  
- **Offline Access:** Saved locations remain available even without internet.

---

## Tech Stack

| Category | Technology |
|-----------|-------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Database** | Room Database |
| **APIs Used** | Google Maps SDK, Places API (New), Geocoding API |
| **Backend** | Google Cloud Platform (GCP) |
| **Version Control** | Git & GitHub |

---

## Implementation Overview

### Google Cloud Setup
1. Created a project named **SpotFinder** in Google Cloud Console.  
2. Enabled APIs:
   - Maps SDK for Android  
   - Places API (New)  
   - Geocoding API  
3. Restricted API key to:
   - `Package name:` `com.example.spotfinder`  
   - `SHA-1 fingerprint:` (for security)  
4. Linked and verified API access for Android.

### Android Implementation
- Built using **Jetpack Compose** for a modern, responsive UI.  
- Integrated **Google Maps Compose library** to display interactive maps.  
- Used **ViewModel + StateFlow** for reactive state management.  
- Stored locations in **Room Database** for offline access.

---

## Project Structure
SpotFinder/
│
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/example/spotfinder/
│ │ │ │ ├── data/ # Room Entities & DAO
│ │ │ │ ├── viewmodel/ # MainViewModel with StateFlow
│ │ │ │ └── MainActivity.kt # Entry point + Compose UI
│ │ │ └── res/values/ # XML resources (no keys exposed)
│ │ └── AndroidManifest.xml
│ ├── build.gradle
│ └── ...
└── README.md

---

## How to Run the Project

1. Clone the repository:  
   ```bash
   git clone https://github.com/<your-username>/SpotFinder.git
2. Open the project in Android Studio (latest version).
3. Add your Google Maps API key in: app/src/main/res/values/google_maps_api.xml
   <string name="google_maps_key">YOUR_API_KEY_HERE</string>
4. Sync Gradle and Run on Emulator or Device.

<div align="center">
  <img src="https://github.com/humanjuan/iOG26/blob/main/app/src/main/res/drawable/ic_iog26.png" alt="iOG26 Logo" width="150">
  <h1>iOG26 Call Filter</h1>
</div>

<div align="center">

[![Buy Me a Coffee](https://img.shields.io/badge/Buy_Me_A_Coffee-Support-orange?logo=buy-me-a-coffee&style=flat-square)](https://www.buymeacoffee.com/humanjuan)

</div>

iOG26 is a simple Android app designed to block unwanted phone calls automatically. It helps you maintain your peace by filtering out spam, anonymous callers, unknown (not-in-contacts) callers, and specific numbers/prefixes you choose to block.

## Key Features

- **Block anonymous/private callers:** Automatically block calls with hidden/withheld caller ID or when the system marks the presentation as restricted/unknown/payphone. This is different from “not in your contacts”.
- **Block unknown numbers (not in contacts):** New. Optionally block any number that is not saved in your contacts. Requires READ_CONTACTS permission.
- **Custom Blocklists:**
    - **Numbers:** Add specific phone numbers to a personal blocklist.
    - **Prefixes:** Block all calls that start with a certain country or area code (e.g., +1-800).
- **History & Dashboard:** A detailed history and a new dashboard with animated charts (blocks per day, caller type breakdown), key metrics (total, avg/day, number of countries, last block date/time), and a Top Countries card.
- **Daily Digest:** Get a daily notification that summarizes all the blocked call activity from the day.
- **Customizable Settings:**
    - Choose whether blocked calls appear in your phone's call log.
    - Decide if you want to receive a silent notification when a call is blocked.
    - Set the time for your daily digest.
- **Internationalization (ES/EN/IT):** Complete translations for Spanish, English and Italian.

### How Call Blocking Works
- The checks are performed in this order:
  1) Is the call anonymous/hidden and is that setting enabled?
  2) Is the number in the blocked numbers list?
  3) Does it start with a blocked prefix?
  4) If the "Block unknown numbers" setting is enabled, is the number NOT in your contacts?

### Privacy
- All decisions are made on-device. Blocklists and history are stored locally using Room.
- The app does not upload your call data to any server.
- Dedicated in-app Privacy Policy screen, localized in Spanish, English, and Italian. The full policy text is bundled offline and shown from Settings.

### Permissions
- READ_CONTACTS: Required only if you enable "Block unknown numbers" (to check whether a caller exists in your contacts). Requested at runtime on first enable.
- POST_NOTIFICATIONS (Android 13+): For optional notifications when a call is blocked and for the daily digest.
- RECEIVE_BOOT_COMPLETED: To re-schedule the daily digest after device reboot.
- Call screening role: The app must be set as the default Caller ID & spam app so Android routes calls to the screening service.

### Limitations & Compatibility
- iOG26 must be set as the default “Caller ID & spam” app; otherwise, Android won’t invoke the screening service.
- Some third‑party VoIP calls may not be routed through Android’s CallScreeningService and cannot be blocked by this app.
- Behavior may vary across OEMs/ROMs. On some devices, silencing plus rejecting improves reliability (the app already attempts this where supported).
- Emergency numbers are never blocked.

## Technical Overview

This section details the app's architecture and the technical flow behind the call-blocking functionality.

### Core Components

- **`CallScreeningService`**: This is the heart of the call-blocking feature. It's an implementation of Android's native `CallScreeningService` API, which allows a designated app to inspect incoming calls and decide whether to allow, reject or silence them before they ring.

- **`Room Database (AppDb)`**: A local database built with Jetpack Room that stores all user-defined rules and app data:
    - **Blocked Numbers:** A list of specific phone numbers to block.
    - **Blocked Prefixes:** A list of prefixes (like country or area codes) to block.
    - **Blocked History:** A log of every call that has been blocked, including the number and timestamp.

- **`BlockRepository`**: Acts as a single source of truth for the app's data. It abstracts the data sources (the Room database) from the rest of the app, providing a clean API for data access.

- **`UI (Jetpack Compose)`**: The entire user interface is built with Jetpack Compose, a modern, declarative UI toolkit. `ViewModel`s are used to manage the state and logic for each screen, exposing data to the UI via `StateFlow`.

- **`WorkManager` + `BroadcastReceiver`**: A periodic/background task schedules the daily digest. On boot, `BootReceiver` re-initializes scheduling. The worker queries recent blocked events and posts a localized summary notification at the user-defined time.

### How Call Blocking Works

The app intervenes in the native call flow at the earliest possible moment, which is key to its effectiveness and seamless operation.

1.  **User Designation:** For the app to function, the user must first set it as their default "Caller ID & spam app" in the phone's settings. This registers the app's `CallScreeningService` with the Android system.

2.  **Incoming Call Trigger:** When the device receives an incoming call, the Android telephony system checks for a registered `CallScreeningService`. Since iOG26 is designated, the system invokes the `onScreenCall` method in our service.

3.  **Real-Time Decision:** Inside `onScreenCall`, the service receives the incoming phone number.
    - The service immediately queries the `BlockRepository` to check the number against the user's rules stored in the Room database.
    - The checks are performed in a specific order: Is it anonymous/private (if enabled)? Is the number in the blocked numbers list? Does it start with a blocked prefix? If "Block unknown numbers" is enabled and the app has READ_CONTACTS permission, is the number not found in the user's contacts?

4.  **Executing the Block:**
    - If any of the rules match, the service constructs a `CallResponse` with `setDisallowCall(true)`. This instructs the system to terminate the call immediately and silently.
    - Based on user settings, the response can also include `setSkipCallLog(true)` to prevent the blocked call from ever appearing in the phone's native call history, making the block truly invisible.
    - If no rules match, the call is allowed to proceed normally without any intervention.

5.  **Logging the Event:** If a call is disallowed, the service records the event by adding a new `BlockedEvent` entry to the Room database. This allows the user to see what the app has done in the "History" screen.

This approach is highly efficient because it leverages a native Android API designed specifically for this purpose. It makes a decision in milliseconds, before the phone even starts ringing, ensuring a non-intrusive experience for the user.

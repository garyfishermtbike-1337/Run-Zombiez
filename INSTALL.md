# Installing Run! Zombiez on your phone

This is a personal debug build, not a Play Store app, so Android will show a couple of warnings during install. That's normal — here's exactly what to do at each step.

## 1. Download the APK

On your phone, open this link in Chrome (or any browser):

**https://github.com/garyfishermtbike-1337/Run-Zombiez/releases/download/v0.2.0-debug/app-debug.apk**

Your browser will download a file called `app-debug.apk` (about 59MB). You'll usually see a download notification appear at the bottom of the screen or in your notification shade — tap it once it finishes.

## 2. Find it in Downloads (if you didn't tap the notification)

If you missed the notification, open the **Files** app (sometimes called "My Files" or "Files by Google") and look in the **Downloads** folder. You'll see `app-debug.apk` there. Tap it.

## 3. Allow installs from this source

The first time you install an app this way, Android will block it and show a message like **"For your security, your phone is not allowed to install unknown apps from this source."**

- Tap **Settings** on that same warning screen.
- You'll see a toggle called **"Allow from this source"** (or similar). Turn it **on**.
- Tap the **back arrow** to return to the install screen.

This only needs to be done once per app source (e.g. once for Chrome, once for Files). You are not turning off Android's security — you're just giving this one app permission to be the one that hands off the install.

## 4. Handle the Play Protect warning

Since this app isn't from the Play Store, **Google Play Protect** may show a screen saying it can't verify the app, or flag it as unrecognized. This is expected for any app installed outside the Play Store, not a sign anything is wrong.

- Tap **Install anyway** (you may need to tap **More details** first to reveal that option).

## 5. Install and open

Tap **Install**. When it finishes, tap **Open** — or find **"Run! Zombiez"** in your app drawer like any other app.

Try the **DEMO** button on the home screen first — it's the fastest way to hear everything working.

---

## Optional: USB debugging, for future installs from a computer

If you (or whoever's helping you) want to install updates directly from a computer using `adb` instead of downloading from GitHub each time, you'll need to turn on **Developer Options** and **USB Debugging** once:

1. Open **Settings → About phone**.
2. Find **Build number** and tap it **7 times** in a row. You'll see a message saying "You are now a developer!"
3. Go back to the main **Settings** screen — you'll now see a new **Developer options** entry (usually under **System**).
4. Open **Developer options** and turn on **USB debugging**.
5. Plug your phone into the computer with a USB cable. A popup will appear on your phone asking **"Allow USB debugging?"** — check **"Always allow from this computer"** and tap **Allow**.

Once that's done, whoever's building the app can install updates straight to your phone with a single command (`adb install app-debug.apk`) instead of you re-downloading the file each time.

You only need to do this if someone asks you to — it's not required just to install and use the app.

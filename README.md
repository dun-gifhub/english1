# Android Studio Skeleton

Project Android Studio moi cho app `Tap Hunter`, dung `Kotlin + Jetpack Compose + Firebase`.

## Mo project

1. Mo Android Studio.
2. Chon `Open`.
3. Mo thu muc: `E:/hsg/android-studio`

## Can bo sung

1. Tai `google-services.json` tu Firebase Console.
2. Dat file vao: `android-studio/app/google-services.json`
3. Bao dam Firebase da bat:
   - Authentication / Email-Password
   - Realtime Database

## Rules Realtime Database goi y

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "auth != null",
        ".write": "auth != null && auth.uid === $uid"
      }
    },
    "friend_codes": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "rooms": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

## Migrate de xuat

1. `AuthRepository.kt`: chuyen login/register tu Kivy sang Android.
2. `HomeScreen.kt`: thay the menu mon hoc va unit.
3. `FriendsScreen.kt`: ket ban, room code, QR, presence.
4. `CallRoomScreen.kt`: sau nay gan WebRTC.

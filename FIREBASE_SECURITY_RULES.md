# Firebase Security Rules — Exammate

Copy rules ini ke Firebase Console → Realtime Database → Rules.

```json
{
  "rules": {
    "nis_lookup": {
      ".read": true,
      ".write": "auth != null",
      "$nis": {
        ".validate": "newData.isString()"
      }
    },
    "users": {
      "$uid": {
        ".read": "auth.uid == $uid",
        ".write": "auth.uid == $uid",
        ".validate": "newData.hasChildren(['nis', 'nama', 'email', 'kelas', 'sekolah', 'role'])"
      }
    },
    "exams": {
      ".read": "auth != null",
      ".write": "root.child('users').child(auth.uid).child('role').val() == 'GURU'",
      "$examId": {
        ".validate": "newData.hasChildren(['mapel', 'tanggal', 'durasiMenit', 'totalSoal', 'soal', 'dibuatOleh'])",
        "dibuatOleh": { ".validate": "newData.val() == auth.uid" }
      }
    },
    "answers": {
      "$examId": {
        "$uid": {
          ".read": "auth.uid == $uid",
          ".write": "auth.uid == $uid"
        }
      }
    },
    "results": {
      "$examId": {
        "$uid": {
          ".read": "auth.uid == $uid || root.child('users').child(auth.uid).child('role').val() == 'GURU'",
          ".write": "auth.uid == $uid"
        }
      }
    }
  }
}
```

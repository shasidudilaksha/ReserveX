# ReserveX book reservations

Run `migrations/001_book_reservations.sql` once in the Supabase SQL Editor.
It creates and seeds the `books` and `reservations` tables.

If migration 001 was already run, also run
`migrations/002_allow_completed_book_reservations.sql`. It allows a
reservation to move to `COMPLETED` after the book is collected.

Pass the project connection values when building the app:

```bash
./gradlew :app:assembleDebug \
  -PSUPABASE_URL=https://YOUR_PROJECT.supabase.co \
  -PSUPABASE_KEY=YOUR_SUPABASE_PUBLISHABLE_KEY
```

The current app keeps its local demo login. The local `SessionManager.userId`
is stored with each reservation, so Supabase Auth is intentionally outside this
assignment scope.

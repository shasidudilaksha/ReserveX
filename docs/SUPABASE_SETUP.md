# Supabase setup for ReserveX

Signup, login, profile editing, and logout use Supabase Auth and the `profiles`
table. Catalog and reservation screens still use demo/local data. No remote
project has been created or modified by these code changes.

## 1. Create the project and tables

1. Sign in at https://supabase.com/dashboard and create a project (or open your existing project).
2. Open **SQL Editor**, create a query, paste all of
   [`202610010001_initial_schema.sql`](../supabase/migrations/202610010001_initial_schema.sql), and run it.
   Run this migration once in a project without existing ReserveX tables. It is a
   transaction: an error rolls back the migration. Do not drop existing tables to rerun it.
3. Run [`202610010002_signup_alignment.sql`](../supabase/migrations/202610010002_signup_alignment.sql)
   to align signup fields and synchronize profile edits. If you already ran the
   initial schema, run **only this second migration**. It is safe to rerun.
4. Optionally run [`seed.sql`](../supabase/seed.sql) to add a sample library, book,
   physical copy, reading area, seat, and room.
5. Open **Table Editor** to confirm these nine public tables exist:

| Table | Stores | Main relationships |
| --- | --- | --- |
| `profiles` | Name, student ID, terms acceptance time | `user_id` refers to Supabase `auth.users` |
| `libraries` | Campus, address, timezone, opening hours | Parent of library resources |
| `books` | Title, author, category, ISBN, description | One title can have many copies |
| `book_copies` | Inventory code, shelf, library, active flag | Refers to a book and library |
| `reading_areas` | Area name and floor | Belongs to a library |
| `seats` | Seat number, grid position, active flag | Belongs to a reading area and its library |
| `meeting_rooms` | Capacity, floor, equipment, active flag | Belongs to a library |
| `reservations` | User, resource, start/end timestamps, status | Exactly one book copy, seat, or room |
| `notifications` | Message, type, timestamp, read flag | Belongs to a user |

Do not create a passwords table. Supabase Auth manages credentials and email in
`auth.users`. A trigger creates a profile for each new Auth user. Signup metadata
uses `full_name`, `student_id`, and `terms_accepted`; student IDs are self-reported,
not verified. The second migration requires these fields on new accounts,
including accounts created from the Dashboard or an Admin API.

| Signup field | Stored in |
| --- | --- |
| Full name | `profiles.full_name` and Auth `raw_user_meta_data.full_name` |
| Student ID | `profiles.student_id` and Auth `raw_user_meta_data.student_id` |
| Email | `auth.users.email` |
| Password | Supabase Auth's password hash; never a public table or local preference |
| Confirm password | Validated locally; never stored |
| Terms checkbox | Auth `raw_user_meta_data.terms_accepted` and server-generated `profiles.terms_accepted_at` |
| Account ID | `auth.users.id` = `profiles.user_id` = app `User.userId` |

Auth user creation and the profile trigger run in the same database transaction.
Profile edits go through Auth metadata; an update trigger keeps the profile aligned.
The email displayed by the app comes from Auth and the name/student ID from `profiles`.
Existing local-only accounts are not migrated: register them again through signup.
Old plaintext password preferences are removed when the app opens the session manager.

In Supabase Authentication, enable email/password signup. If email confirmation
is enabled, configure a valid Site URL under URL Configuration, confirm using
the email link in a browser, then return to the app and sign in. The app does not
need a deep link for this manual flow. Supabase may hide duplicate-email signup
attempts; a confirmation prompt is not proof that a new account was created.
The project password policy can require more than the app's eight-character minimum.

Time slots, available counts, and dashboard statistics are calculated rather than
stored in separate tables. `SELECTED` is a temporary UI state, not a database seat
status. `is_active` means a resource is in service, not necessarily free for a booking.

## 2. Configure the Android connection

Copy the project URL and **publishable key** from the project's **Connect** dialog
or **Settings > API Keys**. Only a key starting with `sb_publishable_` is accepted
by this client. Never use a secret key, service-role key, or database password in Android.

In the repository root, copy `supabase.properties.example` to `supabase.properties`:

```properties
SUPABASE_URL=https://YOUR_PROJECT_REF.supabase.co
SUPABASE_PUBLISHABLE_KEY=sb_publishable_YOUR_KEY
```

Replace both values. Do not surround them with quotes. `supabase.properties` is
ignored by Git. Keep Android SDK settings in `local.properties` separate; this
repository already tracks that file, so do not put credentials there.

Alternatively set OS/CI environment variables with the same names. Nonempty
environment variables take precedence over the properties file. A `.env` file is
**not** loaded by this Android project.

Sync Gradle and rebuild after changing the values. Gradle embeds them in
`BuildConfig`; they are build-time settings, not runtime environment variables.
Publishable keys are visible in APKs, so the database's Row Level Security policies
are essential. The app already has Android's Internet permission.

## 3. Verify the connection

With an emulator or device connected, run this from PowerShell after applying the SQL:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.example.libreserve.data.SupabaseConnectionTest"
```

The smoke test reads one library ID using the configured key. It also succeeds for
an empty library table. With no configuration it is skipped. Invalid settings,
missing tables, network failures, and permission errors fail the test.

For app code, call the client from a coroutine and handle errors in the UI:

```kotlin
import com.example.libreserve.data.SupabaseClient

// Inside a coroutine; networking is dispatched to Dispatchers.IO by the client.
SupabaseClient.checkConnection()
val booksJson = SupabaseClient.select(
    "books",
    mapOf("select" to "book_id,title,author", "order" to "title", "limit" to "50")
)
```

`select` returns a JSON array. Map database snake_case fields into the app's Kotlin
models. Use explicit pagination (`limit`/`offset`) for larger catalogs. The transport
does not cache results or log credentials. `UserRepository` manages Auth calls
and refreshes expired tokens. Tokens are encrypted with Android Keystore;
passwords are not persisted. Profile details are cached locally after being read
from the database. Logout clears the local session even when offline and reports
if server revocation could not complete.

## 4. What remains before the screens use live data

- Pass the signed-in user's access token from `UserRepository.accessToken()` to
  `SupabaseClient.select(..., accessToken = token)` when wiring reservations or notifications.
  Never send a publishable key as a bearer token.
- Convert repository calls and ViewModels to asynchronous loading/error states;
  map UUIDs and database fields instead of using demo IDs such as `lib001`.
- Implement transactional booking/modify/cancel RPCs or a trusted backend. This
  starter schema deliberately denies direct reservation writes from the app.
  The backend must validate active resources, library hours, maximum durations,
  quotas, and cancellation rules. Overlap exclusion constraints already prevent
  two uncancelled bookings for the same physical resource and time range.
- Implement an availability RPC returning only free/busy information or counts.
  A user's reservation SELECT policy cannot see other users' bookings and must
  not be used alone to calculate global availability.
- Compute available copies/seats/rooms from active resources and bookings for the
  requested interval. Use `timestamptz` values with explicit UTC offsets and
  format them in the library's timezone for display. Adjacent reservations are
  allowed: an interval ending at 10:00 does not block one starting at 10:00.
- Create notifications from trusted backend operations and add the write client
  for marking them read. Only `is_read` may be updated by its owner. Completion
  status also needs a backend process; it does not change automatically.

Catalog rows are readable by signed-out and signed-in users. Profiles,
reservations, and notifications are private to their owner. Catalog maintenance
and notification creation are restricted to Dashboard/trusted backend operations.
Do not disable RLS to fix an empty response.

## Signup verification

1. Apply both migrations and configure the app's project URL/publishable key.
2. Register a new test email with a name, student ID, matching passwords, and accepted terms.
3. Check Authentication > Users for its email/UUID and `public.profiles` for the
   matching `user_id`, `full_name`, `student_id`, and `terms_accepted_at`.
4. Confirm the email if required, then sign in. Profile should display the saved name/email.
5. Edit the name, save, and verify the same change in `profiles` and Auth metadata.
6. Sign out and check that the old demo credentials cannot sign in without a real Auth account.

Run the signup field-mapping tests locally (no accounts created):

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

With a device connected, run the encrypted-session storage test:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.example.libreserve.data.SessionVaultTest"
```

## References

- [Supabase Android guide](https://supabase.com/docs/guides/getting-started/quickstarts/kotlin)
- [API keys](https://supabase.com/docs/guides/getting-started/api-keys)
- [User profiles and Auth](https://supabase.com/docs/guides/auth/managing-user-data)
- [Row Level Security](https://supabase.com/docs/guides/database/postgres/row-level-security)

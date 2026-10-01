-- Run once in a NEW Supabase project's SQL Editor. All changes are transactional.
begin;

create extension if not exists btree_gist with schema extensions;
set local search_path = public, extensions;

create table public.profiles (
    user_id uuid primary key references auth.users(id) on delete cascade,
    full_name text not null default '',
    student_id text,
    created_at timestamptz not null default now()
);

-- Email and passwords are managed by Supabase Auth, not public tables.
create function public.handle_new_user() returns trigger
language plpgsql security definer set search_path = '' as $$
begin
    insert into public.profiles (user_id, full_name, student_id)
    values (new.id, coalesce(new.raw_user_meta_data ->> 'full_name', ''),
            nullif(new.raw_user_meta_data ->> 'student_id', ''));
    return new;
end;
$$;
revoke all on function public.handle_new_user() from public, anon, authenticated;
create trigger on_auth_user_created after insert on auth.users
    for each row execute function public.handle_new_user();
-- Include any accounts created before this migration.
insert into public.profiles (user_id, full_name, student_id)
select id, coalesce(raw_user_meta_data ->> 'full_name', ''),
       nullif(raw_user_meta_data ->> 'student_id', '') from auth.users;

create table public.libraries (
    library_id uuid primary key default gen_random_uuid(),
    name text not null,
    campus text not null,
    address text not null,
    timezone text not null default 'Asia/Colombo',
    open_time time not null default '08:00',
    close_time time not null default '21:00',
    check (close_time > open_time)
);

create table public.books (
    book_id uuid primary key default gen_random_uuid(),
    title text not null,
    author text not null,
    category text not null,
    isbn text unique,
    description text not null default ''
);

create table public.book_copies (
    copy_id uuid primary key default gen_random_uuid(),
    book_id uuid not null references public.books(book_id),
    library_id uuid not null references public.libraries(library_id),
    inventory_code text not null unique,
    shelf_location text not null,
    is_active boolean not null default true,
    unique (copy_id, library_id)
);

create table public.reading_areas (
    area_id uuid primary key default gen_random_uuid(),
    library_id uuid not null references public.libraries(library_id),
    name text not null,
    floor integer not null default 0,
    unique (area_id, library_id),
    unique (library_id, name)
);

create table public.seats (
    seat_id uuid primary key default gen_random_uuid(),
    area_id uuid not null,
    library_id uuid not null,
    seat_number text not null,
    row_index integer not null check (row_index >= 0),
    column_index integer not null check (column_index >= 0),
    is_active boolean not null default true,
    foreign key (area_id, library_id) references public.reading_areas(area_id, library_id),
    unique (seat_id, library_id),
    unique (area_id, seat_number),
    unique (area_id, row_index, column_index)
);

create table public.meeting_rooms (
    room_id uuid primary key default gen_random_uuid(),
    library_id uuid not null references public.libraries(library_id),
    name text not null,
    capacity integer not null check (capacity > 0),
    floor integer not null default 0,
    location text not null,
    equipment text[] not null default '{}',
    is_active boolean not null default true,
    unique (room_id, library_id),
    unique (library_id, name)
);

create table public.reservations (
    reservation_id uuid primary key default gen_random_uuid(),
    user_id uuid not null default auth.uid() references public.profiles(user_id) on delete cascade,
    library_id uuid not null references public.libraries(library_id),
    type text not null check (type in ('BOOK', 'SEAT', 'MEETING_ROOM')),
    book_copy_id uuid,
    seat_id uuid,
    room_id uuid,
    starts_at timestamptz not null,
    ends_at timestamptz not null,
    status text not null default 'UPCOMING' check (status in ('UPCOMING', 'COMPLETED', 'CANCELLED')),
    created_at timestamptz not null default now(),
    foreign key (book_copy_id, library_id) references public.book_copies(copy_id, library_id),
    foreign key (seat_id, library_id) references public.seats(seat_id, library_id),
    foreign key (room_id, library_id) references public.meeting_rooms(room_id, library_id),
    check (ends_at > starts_at),
    check (
        (type = 'BOOK' and book_copy_id is not null and seat_id is null and room_id is null) or
        (type = 'SEAT' and seat_id is not null and book_copy_id is null and room_id is null) or
        (type = 'MEETING_ROOM' and room_id is not null and book_copy_id is null and seat_id is null)
    ),
    -- Prevent concurrent double bookings, including requests by different users.
    exclude using gist (book_copy_id with =, tstzrange(starts_at, ends_at, '[)') with &&)
        where (status <> 'CANCELLED'),
    exclude using gist (seat_id with =, tstzrange(starts_at, ends_at, '[)') with &&)
        where (status <> 'CANCELLED'),
    exclude using gist (room_id with =, tstzrange(starts_at, ends_at, '[)') with &&)
        where (status <> 'CANCELLED')
);
create index reservations_user_start on public.reservations(user_id, starts_at);
create index book_copies_book on public.book_copies(book_id, library_id);

create table public.notifications (
    notification_id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles(user_id) on delete cascade,
    title text not null,
    message text not null,
    type text not null check (type in ('BOOK', 'SEAT', 'MEETING_ROOM')),
    is_read boolean not null default false,
    created_at timestamptz not null default now()
);
create index notifications_user_created on public.notifications(user_id, created_at desc);

-- Catalog is publicly readable; only the Dashboard/trusted backend can maintain it.
do $$
declare table_name text;
begin
    foreach table_name in array array['libraries', 'books', 'book_copies', 'reading_areas', 'seats', 'meeting_rooms']
    loop
        execute format('alter table public.%I enable row level security', table_name);
        execute format('revoke all on public.%I from anon, authenticated', table_name);
        execute format('grant select on public.%I to anon, authenticated', table_name);
        execute format('create policy catalog_read on public.%I for select to anon, authenticated using (true)', table_name);
    end loop;
end;
$$;

alter table public.profiles enable row level security;
alter table public.reservations enable row level security;
alter table public.notifications enable row level security;
revoke all on public.profiles, public.reservations, public.notifications from anon, authenticated;
grant select on public.profiles, public.reservations, public.notifications to authenticated;
grant update (full_name, student_id) on public.profiles to authenticated;
grant update (is_read) on public.notifications to authenticated;

create policy profile_read on public.profiles for select to authenticated
    using ((select auth.uid()) = user_id);
create policy profile_update on public.profiles for update to authenticated
    using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy reservation_read on public.reservations for select to authenticated
    using ((select auth.uid()) = user_id);
create policy notification_read on public.notifications for select to authenticated
    using ((select auth.uid()) = user_id);
create policy notification_update on public.notifications for update to authenticated
    using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);

-- Booking writes deliberately require a trusted backend/RPC to validate opening
-- hours, active resources, loan duration and quotas. No client write grant yet.
commit;

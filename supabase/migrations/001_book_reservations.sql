create table if not exists public.books (
    id text primary key,
    title text not null,
    author text not null,
    category text not null,
    isbn text,
    description text,
    shelf_location text,
    total_copies integer not null check (total_copies >= 0)
);

create table if not exists public.reservations (
    id text primary key,
    user_id text not null,
    book_id text not null references public.books(id) on delete cascade,
    pickup_date text not null,
    pickup_time text not null,
    status text not null default 'ACTIVE' check (status in ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    created_at timestamptz not null default now()
);

alter table public.books enable row level security;
alter table public.reservations enable row level security;

grant select on public.books to anon, authenticated;
grant select, insert, update on public.reservations to anon, authenticated;

drop policy if exists "Books can be read by app users" on public.books;
create policy "Books can be read by app users"
    on public.books for select
    to anon, authenticated
    using (true);

drop policy if exists "Reservations can be read by app users" on public.reservations;
create policy "Reservations can be read by app users"
    on public.reservations for select
    to anon, authenticated
    using (true);

drop policy if exists "Reservations can be created by app users" on public.reservations;
create policy "Reservations can be created by app users"
    on public.reservations for insert
    to anon, authenticated
    with check (true);

drop policy if exists "Reservations can be cancelled by app users" on public.reservations;
create policy "Reservations can be cancelled by app users"
    on public.reservations for update
    to anon, authenticated
    using (true)
    with check (status in ('ACTIVE', 'COMPLETED', 'CANCELLED'));

insert into public.books (id, title, author, category, isbn, description, shelf_location, total_copies)
values
('book001', 'Clean Code', 'Robert C. Martin', 'Software Engineering', '978-0132350884', 'A handbook of agile software craftsmanship.', 'SE-102', 3),
('book002', 'Introduction to Algorithms', 'Thomas H. Cormen', 'Computer Science', '978-0262033848', 'A comprehensive introduction to algorithms.', 'CS-201', 2),
('book003', 'Database System Concepts', 'Abraham Silberschatz', 'Database Systems', '978-0078022159', 'Coverage of database design and systems.', 'DB-105', 4),
('book004', 'Computer Networks', 'Andrew S. Tanenbaum', 'Networking', '978-0132126953', 'An examination of computer networking.', 'NW-301', 2),
('book005', 'Design Patterns', 'Erich Gamma', 'Software Engineering', '978-0201633610', 'Reusable and flexible object-oriented design.', 'SE-110', 3),
('book006', 'The Pragmatic Programmer', 'David Thomas', 'Software Engineering', '978-0135957059', 'Writing robust, adaptable, high-quality code.', 'SE-115', 2),
('book007', 'Artificial Intelligence: A Modern Approach', 'Stuart Russell', 'Artificial Intelligence', '978-0134610993', 'An introduction to artificial intelligence.', 'AI-401', 3),
('book008', 'Operating System Concepts', 'Abraham Silberschatz', 'Operating Systems', '978-1118063330', 'Foundational operating system concepts.', 'OS-205', 4),
('book009', 'Discrete Mathematics and Its Applications', 'Kenneth H. Rosen', 'Mathematics', '978-0073383095', 'A tool for mastering discrete mathematics.', 'MA-102', 5),
('book010', 'Software Engineering', 'Ian Sommerville', 'Software Engineering', '978-0133943030', 'An introduction to software engineering.', 'SE-120', 3),
('book011', 'Data Structures and Algorithms in Java', 'Robert Lafore', 'Computer Science', '978-0672324536', 'Data structures and algorithms using Java.', 'CS-210', 2),
('book012', 'Computer Organization and Architecture', 'William Stallings', 'Computer Architecture', '978-0134101613', 'An introduction to computer organization.', 'CA-301', 3)
on conflict (id) do nothing;

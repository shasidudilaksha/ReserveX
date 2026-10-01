-- Optional small catalog for testing; safe to run repeatedly after the migration.
begin;
insert into public.libraries (library_id, name, campus, address)
values ('10000000-0000-0000-0000-000000000001', 'Demo Library', 'Demo Campus', 'Colombo')
on conflict do nothing;
insert into public.books (book_id, title, author, category, isbn, description)
values ('20000000-0000-0000-0000-000000000001', 'Demo Book', 'ReserveX', 'Computer Science', null, 'Sample catalog entry')
on conflict do nothing;
insert into public.book_copies (copy_id, book_id, library_id, inventory_code, shelf_location)
values ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001',
        '10000000-0000-0000-0000-000000000001', 'DEMO-001', 'CS-101')
on conflict do nothing;
insert into public.reading_areas (area_id, library_id, name, floor)
values ('40000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Reading Area A', 1)
on conflict do nothing;
insert into public.seats (seat_id, area_id, library_id, seat_number, row_index, column_index)
values ('50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001',
        '10000000-0000-0000-0000-000000000001', 'A-1', 0, 0)
on conflict do nothing;
insert into public.meeting_rooms (room_id, library_id, name, capacity, floor, location, equipment)
values ('60000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001',
        'Meeting Room A', 6, 1, 'Floor 1', array['Whiteboard', 'Wi-Fi'])
on conflict do nothing;
commit;

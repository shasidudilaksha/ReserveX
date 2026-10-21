-- Allow the app to mark a book as collected/completed after pickup.
alter table public.reservations
    drop constraint if exists reservations_status_check;

alter table public.reservations
    add constraint reservations_status_check
    check (status in ('ACTIVE', 'COMPLETED', 'CANCELLED'));

drop policy if exists "Reservations can be cancelled by app users" on public.reservations;
create policy "Reservations can be updated by app users"
    on public.reservations for update
    to anon, authenticated
    using (true)
    with check (status in ('ACTIVE', 'COMPLETED', 'CANCELLED'));

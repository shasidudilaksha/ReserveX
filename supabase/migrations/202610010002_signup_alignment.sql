-- Apply AFTER 202610010001_initial_schema.sql, including on existing projects.
begin;

alter table public.profiles add column if not exists terms_accepted_at timestamptz;

-- Keep the original Auth user insert and the profile insert in one transaction.
-- If profile creation fails, Supabase signup fails rather than creating an orphan.
create or replace function public.handle_new_user() returns trigger
language plpgsql security definer set search_path = '' as $$
begin
    if nullif(btrim(new.raw_user_meta_data ->> 'full_name'), '') is null
        or nullif(btrim(new.raw_user_meta_data ->> 'student_id'), '') is null
        or (new.raw_user_meta_data -> 'terms_accepted') is distinct from 'true'::jsonb then
        raise exception 'Full name, student ID and terms acceptance are required';
    end if;
    insert into public.profiles (user_id, full_name, student_id, terms_accepted_at)
    values (new.id, btrim(new.raw_user_meta_data ->> 'full_name'),
        btrim(new.raw_user_meta_data ->> 'student_id'), now());
    return new;
end;
$$;

create or replace function public.sync_profile_from_auth() returns trigger
language plpgsql security definer set search_path = '' as $$
begin
    if new.raw_user_meta_data is distinct from old.raw_user_meta_data then
        update public.profiles set
            full_name = coalesce(nullif(btrim(new.raw_user_meta_data ->> 'full_name'), ''), full_name),
            student_id = coalesce(nullif(btrim(new.raw_user_meta_data ->> 'student_id'), ''), student_id)
        where user_id = new.id;
    end if;
    return new;
end;
$$;
revoke all on function public.handle_new_user() from public, anon, authenticated;
revoke all on function public.sync_profile_from_auth() from public, anon, authenticated;
drop trigger if exists on_auth_user_updated on auth.users;
create trigger on_auth_user_updated after update of raw_user_meta_data on auth.users
    for each row execute function public.sync_profile_from_auth();

-- Backfill missing profiles only; never invent consent for older accounts.
insert into public.profiles (user_id, full_name, student_id)
select id, coalesce(raw_user_meta_data ->> 'full_name', ''),
    nullif(raw_user_meta_data ->> 'student_id', '') from auth.users
on conflict (user_id) do nothing;

-- All profile edits now go through Auth metadata, keeping the two records aligned.
revoke update (full_name, student_id) on public.profiles from authenticated;
drop policy if exists profile_update on public.profiles;
commit;

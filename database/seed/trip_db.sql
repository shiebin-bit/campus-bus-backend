create table if not exists trips (
    id bigserial primary key,
    bus_id bigint not null,
    driver_id bigint not null,
    start_time timestamp(6) with time zone not null,
    end_time timestamp(6) with time zone,
    status varchar(255) not null
);

insert into trips (id, bus_id, driver_id, start_time, end_time, status)
values
    (301, 201, 3, now() - interval '18 minutes', null, 'ACTIVE'),
    (302, 203, 4, now() - interval '12 minutes', null, 'ACTIVE'),
    (303, 205, 5, now() - interval '9 minutes', null, 'ACTIVE'),
    (304, 204, 2, now() - interval '55 minutes', now() - interval '10 minutes', 'COMPLETED')
on conflict (id) do update set
    bus_id = excluded.bus_id,
    driver_id = excluded.driver_id,
    start_time = excluded.start_time,
    end_time = excluded.end_time,
    status = excluded.status;

select setval(pg_get_serial_sequence('trips', 'id'), greatest((select max(id) from trips), 1));

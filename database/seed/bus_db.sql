create table if not exists buses (
    id bigserial primary key,
    bus_code varchar(255) not null unique,
    plate_number varchar(255) not null unique,
    route_id bigint not null
);

insert into buses (id, bus_code, plate_number, route_id)
values
    (201, 'BUS-A01', 'CB-A0101', 101),
    (202, 'BUS-A02', 'CB-A0102', 101),
    (203, 'BUS-H01', 'CB-H0101', 102),
    (204, 'BUS-E01', 'CB-E0101', 103),
    (205, 'BUS-M01', 'CB-M0101', 104),
    (206, 'BUS-N01', 'CB-N0101', 105),
    (207, 'BUS-E02', 'CB-E0102', 103)
on conflict (id) do update set
    bus_code = excluded.bus_code,
    plate_number = excluded.plate_number,
    route_id = excluded.route_id;

select setval(pg_get_serial_sequence('buses', 'id'), greatest((select max(id) from buses), 1));

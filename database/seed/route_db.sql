create table if not exists routes (
    id bigserial primary key,
    route_name varchar(255) not null unique,
    description varchar(255) not null
);

insert into routes (id, route_name, description)
values
    (101, 'Campus Loop A', 'Main campus circular route covering academic blocks and library'),
    (102, 'Hostel Express', 'Direct route between student hostels and central campus'),
    (103, 'Engineering Shuttle', 'Shuttle route for engineering faculty and labs'),
    (104, 'Medical Library Line', 'Connects clinic, medical faculty, library, and central cafe'),
    (105, 'Evening Safety Route', 'Evening route for safe travel between late-night study areas and residences')
on conflict (id) do update set
    route_name = excluded.route_name,
    description = excluded.description;

select setval(pg_get_serial_sequence('routes', 'id'), greatest((select max(id) from routes), 1));

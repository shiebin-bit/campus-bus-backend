create table if not exists stops (
    id bigserial primary key,
    route_id bigint not null,
    stop_name varchar(255) not null,
    latitude double precision not null,
    longitude double precision not null,
    sequence_no integer not null
);

insert into stops (id, route_id, stop_name, latitude, longitude, sequence_no)
values
    (1001, 101, 'Main Gate', 2.94410, 101.87410, 1),
    (1002, 101, 'Library Square', 2.94520, 101.87540, 2),
    (1003, 101, 'Student Center', 2.94610, 101.87620, 3),
    (1004, 101, 'Sports Complex', 2.94700, 101.87710, 4),
    (1005, 101, 'North Lecture Hall', 2.94800, 101.87800, 5),
    (1006, 102, 'Hostel A', 2.94120, 101.87130, 1),
    (1007, 102, 'Hostel B', 2.94200, 101.87210, 2),
    (1008, 102, 'Hostel C', 2.94270, 101.87270, 3),
    (1009, 102, 'Food Court', 2.94330, 101.87320, 4),
    (1010, 102, 'Central Campus', 2.94500, 101.87500, 5),
    (1011, 103, 'Engineering Gate', 2.94820, 101.87850, 1),
    (1012, 103, 'Workshop Block', 2.94910, 101.87930, 2),
    (1013, 103, 'Innovation Lab', 2.95000, 101.88010, 3),
    (1014, 103, 'Lecture Hall E', 2.95100, 101.88100, 4),
    (1015, 103, 'Robotics Lab', 2.95180, 101.88170, 5),
    (1016, 104, 'Campus Clinic', 2.94470, 101.87940, 1),
    (1017, 104, 'Medical Faculty', 2.94560, 101.88020, 2),
    (1018, 104, 'Research Library', 2.94660, 101.88090, 3),
    (1019, 104, 'Central Cafe', 2.94740, 101.88160, 4),
    (1020, 104, 'Admin Annex', 2.94830, 101.88230, 5),
    (1021, 105, 'Library Night Stop', 2.94530, 101.87560, 1),
    (1022, 105, 'Graduate Centre', 2.94630, 101.87680, 2),
    (1023, 105, 'Security Office', 2.94720, 101.87790, 3),
    (1024, 105, 'Hostel Guard House', 2.94370, 101.87380, 4),
    (1025, 105, 'Main Gate Night Stop', 2.94420, 101.87420, 5)
on conflict (id) do update set
    route_id = excluded.route_id,
    stop_name = excluded.stop_name,
    latitude = excluded.latitude,
    longitude = excluded.longitude,
    sequence_no = excluded.sequence_no;

select setval(pg_get_serial_sequence('stops', 'id'), greatest((select max(id) from stops), 1));

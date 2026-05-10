create table if not exists users (
    id bigserial primary key,
    name varchar(255) not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(255) not null
);

insert into users (id, name, email, password, role)
values
    (1, 'System Admin', 'admin@campusbus.com', '$2a$10$ZAO35J3DZzNA/Fya8WEqSOKDsn4EL2YRy7UJnTjtMdhr2q/1o4VEi', 'ADMIN'),
    (2, 'Campus Driver', 'driver@campusbus.com', '$2a$10$qhDNSVpishFoCs.ChEKTCuNHlw5rmkhN8dzyw9ZOPgJ1cgSI5g1cK', 'DRIVER'),
    (3, 'Nora Campus Driver', 'nora.driver@campusbus.com', '$2a$10$qhDNSVpishFoCs.ChEKTCuNHlw5rmkhN8dzyw9ZOPgJ1cgSI5g1cK', 'DRIVER'),
    (4, 'Ravi Campus Driver', 'ravi.driver@campusbus.com', '$2a$10$qhDNSVpishFoCs.ChEKTCuNHlw5rmkhN8dzyw9ZOPgJ1cgSI5g1cK', 'DRIVER'),
    (5, 'Aina Campus Driver', 'aina.driver@campusbus.com', '$2a$10$qhDNSVpishFoCs.ChEKTCuNHlw5rmkhN8dzyw9ZOPgJ1cgSI5g1cK', 'DRIVER')
on conflict (id) do update set
    name = excluded.name,
    email = excluded.email,
    password = excluded.password,
    role = excluded.role;

select setval(pg_get_serial_sequence('users', 'id'), greatest((select max(id) from users), 1));

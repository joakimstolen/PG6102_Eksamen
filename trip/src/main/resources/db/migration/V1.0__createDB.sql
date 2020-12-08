create table trips (
    trip_id varchar(255) not null,
    place varchar(255) not null,
    duration_days integer not null,
    price_per_person integer not null,

    primary key (trip_id)
);
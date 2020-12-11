create table trips (
    trip_id varchar(255) not null,
    place varchar(255) not null,
    duration_days integer not null,
    price_per_person integer not null,

    primary key (trip_id)
);

-- https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-08/scores/src/main/resources/db/migration/V1.0__createDB.sql
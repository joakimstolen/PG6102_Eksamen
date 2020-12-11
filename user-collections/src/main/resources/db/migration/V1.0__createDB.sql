create sequence hibernate_sequence start with 1 increment by 1;

create table booked_trip (
    id bigint not null,
    number_of_people_booked integer not null check (number_of_people_booked>=0),
    number_of_trips integer not null check (number_of_trips>=0),
    trip_id varchar(255),
    user_user_id varchar(255),
    primary key (id));

create table user_data (
    user_id varchar(255) not null,
    coins integer not null check (coins>=0),
    nr_of_persons integer not null check (nr_of_persons>=0),
    primary key (user_id));

alter table booked_trip add constraint FK81d7uk3xgfyurglogki3o6crg foreign key (user_user_id) references user_data;


--https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/resources/db/migration/V1.0__createDB.sql
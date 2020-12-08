create sequence hibernate_sequence start with 1 increment by 1;

create table booked_trip (
    id bigint not null,
    trip_id varchar(255),
    number_of_people_booked integer not null check (number_of_people_booked >= 0),
    number_of_trips integer not null check (number_of_trips >= 0),
    user_user_id varchar(255) not null,
    primary key(id)
);


create table user_data (
    user_id varchar(255) not null,
    coins integer not null,
    nr_of_persons integer not null,
    primary key(user_id)
);

alter table booked_trip add constraint FKtco9dei78cocpwi1sxye9mw3b foreign key (user_user_id) references user_data;
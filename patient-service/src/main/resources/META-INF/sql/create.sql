create table if not exists reactive_db.patients (id bigserial not null constraint patients_pk primary key, first_name text, last_name  text, username text,age integer);
alter table reactive_db.patients owner to postgres;
create table reactive_db.favorite_doctors(patient_id bigint not null constraint fk references reactive_db.patients, first_name  varchar(255), id  bigint,last_name varchar(255),username varchar(255), unique(patient_id, id, first_name, last_name, username));
alter table reactive_db.favorite_doctors owner to postgres;alter table reactive_db.favorite_doctors owner to postgres;

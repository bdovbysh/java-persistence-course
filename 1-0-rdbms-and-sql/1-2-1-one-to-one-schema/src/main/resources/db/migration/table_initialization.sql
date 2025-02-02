/*

User profile database stores information about users and their work profiles.

Each user has one and only one work profile.

Each user has stored first and last names, email and birthday which are mandatory. Email is a unique value.
A profile for each user is optional, and consists of optional information: city, job_position, company and education.
All these fields are regular strings without restrictions.

  TECH NOTES AND NAMING CONVENTION
- All tables, columns and constraints are named using "snake case" naming convention
- All table names must be plural (e.g. "companies", not "company")
- All tables (except link tables) should have a single-value identifier of type BIGINT, which is a primary key
- All primary key, foreign key, and unique constraint should be named according to the naming convention.
- All "1 - optional 1" relations should be handled using the same primary key value for both tables. E.g. child table
should have a column that stores primary key from a parent table, which is a foreign key and primary key at the same time

- All primary keys should be named according to the following rule "table_name_PK"
- All foreign keys should be named according to the following rule "table_name_reference_table_name_FK"
- All alternative keys (unique) should be named according to the following rule "table_name_column_name_AK"

*/

-- TODO: implement the SQL according to the description
CREATE TABLE if not exists users (
    id bigint,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) not null,
    birthday DATE NOT NULL,
    constraint users_PK primary key (id),
    constraint users_email_AK unique (email)
);


CREATE TABLE if not exists profiles (
    user_id bigint,
    job_position VARCHAR(255),
    company varchar(255),
    education VARCHAR(255),
    city VARCHAR(255),
    constraint profiles_PK primary key (user_id),
    constraint profiles_users_FK foreign key (user_id) references users
);

/*
    * Creates an {@code account} table. That table has a identifier column {@code id} with type {@code bigint}.
    * It also contains an {@code email} column that is mandatory and should have unique value. This column is be able
    * to store any valid email. The table also has columns {@code first_name}, {@code last_name}, and {@code gender}
    * that are typical string columns with 255 characters, and are mandatory. Account {@code birthday} is stored
    * in the {@code DATE} mandatory column. The value of account balance is not mandatory, and is stored
    * in the {@code balance} column that is a {@code DECIMAL} number with {@code precision = 19} ,
    * and {@code scale = 4}. A column {@code creation_time} stores a {@code TIMESTAMP}, is mandatory, and has a default
    * value that is set to the current timestamp using database function {@code now()}. Table primary key
    * is an {@code id}, and corresponding constraint is named {@code "account_pk"}. An unique constraint that
    * is created for {@code email column} is called "account_email_uq"
*/

CREATE TABLE account (
    id BIGINT,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    gender VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    balance DECIMAL(19,4),
    creation_time TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT account_pk PRIMARY KEY (id),
    CONSTRAINT account_email_uq UNIQUE (email)
);




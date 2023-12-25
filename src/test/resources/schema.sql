/*! MySQL-specific code */

create table jwt_token (
        id bigint not null auto_increment,
        access_token varchar(255) not null,
        refresh_token varchar(255) not null,
        refresh_token_expiry_date datetime(6) not null,
        user_id bigint,
        primary key (id)
    );

create table role (
        id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    );

create table `user` (
        id bigint not null auto_increment,
        created_by varchar(255),
        created_on datetime(6) not null,
        last_modified_by varchar(255),
        last_modified_on datetime(6),
        first_name varchar(255),
        is_account_expired bit not null,
        is_account_locked bit not null,
        is_credentials_expired bit not null,
        is_enabled bit not null,
        last_name varchar(255),
        password varchar(255),
        username varchar(255),
        api_vendor_id bigint,
        primary key (id)
    );

create table user_attempt (
        id bigint not null auto_increment,
        attempt integer not null,
        ip_address varchar(255),
        last_modified_on datetime(6),
        username varchar(255),
        primary key (id)
    );

create table users_roles (
        user_id bigint not null,
        role_id bigint not null,
        primary key (user_id, role_id)
    );
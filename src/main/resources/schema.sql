--create table users(username varchar_ignorecase(50) not null primary key,password varchar_ignorecase(500) not null,enabled boolean not null);
CREATE TABLE Users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  userName VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(500) NOT NULL,
  enabled BOOLEAN NOT NULL
);

create table authorities (userName varchar_ignorecase(50) not null,authority varchar_ignorecase(50) not null,constraint fk_authorities_users foreign key(userName) references users(userName));
create unique index ix_auth_username on authorities (userName,authority);
--ALTER TABLE users ADD COLUMN email VARCHAR_IGNORECASE(100);

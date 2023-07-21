drop table if exists friends , mpa, films, genres, films_genres , film_likes, directors, films_directors,
 reviews, like_review, feed, users;

create table if not exists users (
	id integer generated by default as identity not null primary key,
	email varchar (50) not null unique,
	login varchar (50) not null unique,
	name varchar (50),
	birthday date
);

create table if not exists friends (
	user_id integer references users (id) on delete cascade,
	friend_id integer references users (id) on delete cascade,
	UNIQUE (user_id, friend_id)
);

create table if not exists mpa (
	id integer  not null primary key,
	name varchar (10)
);

create table if not exists films (
	id integer generated by default as identity not null primary key,
	name varchar (50) not null unique,
	release_date date,
	description varchar (200),
	duration integer,
	rate integer,
	mpa_id integer references mpa (id) on delete cascade
);

create table if not exists genres (
	id integer not null primary key,
	name varchar (50)
);

create table if not exists films_genres (
	film_id integer references films (id) on delete cascade,
	genre_id integer references genres (id) on delete cascade,
	unique (film_id, genre_id)
);

create table if not exists film_likes (
	film_id integer references films (id) on delete cascade,
	user_id integer references users (id) on delete cascade,
	UNIQUE (user_id, film_id)
);

-- новые таблицы для ТЗ 12
-- для режиссера
create table if not exists directors (
	id integer generated by default as identity not null primary key,
	name varchar (100) unique not null
);

create table if not exists films_directors (
	film_id integer not null,
	director_id integer not null,
	CONSTRAINT FILMS_DIRECTORS_PK PRIMARY KEY (film_id, director_id),
	CONSTRAINT FILMS_DIRECTORS_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS (ID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FILMS_DIRECTORS_FK_1 FOREIGN KEY (director_id) REFERENCES DIRECTORS (ID) ON DELETE CASCADE ON UPDATE CASCADE
);

-- для отзывов
create table if not exists reviews (
    id integer generated by default as identity not null primary key,
    content varchar (200),
    is_positive boolean,
    user_id integer references users (id) on delete cascade,
    film_id integer references films (id) on delete cascade
);

create table if not exists like_review (
    review_id integer references reviews (id) on delete cascade,
    user_id integer references users (id) on delete cascade,
    type integer,
    UNIQUE (review_id, user_id)
);

-- для ленты событий
create table if not exists feed (
    event_id integer generated by default as identity not null primary key,
    time_stamp timestamp,
    user_id integer references users (id) on delete cascade,
    event_type varchar (255), -- пока так, надо еще подумать
    operation varchar (255), -- пока так, надо еще подумать
    entity_id integer -- с этим недо будет разобраться
);
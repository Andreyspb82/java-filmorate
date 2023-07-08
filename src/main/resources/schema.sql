drop table if exists users, friends , mpa, films, genres, films_genres , film_likes;

create table if not exists users (
	id integer generated by default as identity not null primary key,
	email varchar not null unique,
	login varchar not null unique,
	name varchar,
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
	name varchar not null unique,
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
	film_id integer references films (id) on delete cascade ,
	user_id integer references users (id) on delete cascade,
	UNIQUE (user_id, film_id)
);
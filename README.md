# java-filmorate
Template repository for Filmorate project.

# Диаграмма базы данных
![ER-диаграмма](https://github.com/Andreyspb82/java-filmorate/blob/add-database/Untitled%20(2).png)
---
## Таблица *films* содержит данные о фильмах
### Поля таблицы:
* **id** - первичны ключ (идентефикатор фильма),  генерируется автоматически при создании фильма;
* **name** - название фильма;
* **description** - описание фильма (максимальная длина описания - 200 символов);
* **release_date** - дата релиза;
* **duration** - продолжительность фильма;
* **rating_id** - возрастной рейтинг фильма.
---
## Таблица *ratings* содержит список возрастных рейтингов фильмов
### Поля таблицы:
* **id** - id рейтинга;
* **rating** - значения рейтинга.
---
## Таблица *genres* содержит список жанров фильмов
### Поля таблицы:
* **id** - id жанра;
* **genre** - значения жанра (у одного фильма может быть несколько значений).
---
## Таблица *films_genres* содержит данные о жанрах фильмов
### Поля таблицы:
* **film_id** - id фильма;
* **genre_id** - id жанра.
---
## Таблица *films_likes* содержит данные о лайках к фильмам
### Поля таблицы:
* **film_id** - id фильма;
* **user_id** - id пользователя.
---
## Таблица *users* содержит данные о пользователях
### Поля таблицы:
* **id** - первичны ключ (идентефикатор пользователя),  генерируется автоматически при создании пользователя;
* **email** - адрес электронной почты пользоватлея, не может быть пустым;
* **login** - логин пользователя, не может быть пустым;
* **name** - имя пользователя;
* **birthday** - день рождения пользователя.
---
## Таблица *friends* содержит данные о добавлении в друзья
### Поля таблицы:
* **id_user** - id пользователя к которому добавился друг;
* **id_friend** - id пользователя добавившегося в друзья.
---


  







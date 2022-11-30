package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.DBFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.DBGenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.DBRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.DBUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final DBUserStorage userStorage;
    private final DBGenreStorage genreStorage;
    private final DBRatingStorage mpaStorage;
    private final DBFilmStorage filmStorage;

    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforEACH() {
        String sql = "DELETE FROM FILMLIKES";
        jdbcTemplate.update(sql);
        String sql1 = "DELETE FROM FILMSGENRE";
        jdbcTemplate.update(sql1);
        String sql2 = "DELETE FROM FRIENDSHIP";
        jdbcTemplate.update(sql2);
        String sql3 = "DELETE FROM USERS";
        jdbcTemplate.update(sql3);
        String sql4 = "DELETE FROM FILMS";
        jdbcTemplate.update(sql4);
    }

    @Test
    public void shouldGetUser() {
        User user1 = createFirstUser();
        int id = user1.getId();
        Optional<User> userOptional = userStorage.getUserById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "firstuser")
                );
    }

    @Test
    public void shouldUserUpdate() {
        User user1 = createFirstUser();
        user1.setEmail("update@mail.ru");
        user1 = userStorage.updateUser(user1);
        Optional<User> userOptional = userStorage.getUserById(user1.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "update@mail.ru")
                );
    }

    @Test
    public void shouldUserListReturn() {
        User user1 = createFirstUser();
        User user2 = createSecondUser();
        List<User> userList = userStorage.getUsers();
        Assertions.assertTrue(userList.size() == 2, "Количество пользователей неверно!");
        Assertions.assertEquals("firstuser", userList.get(0).getName());
        Assertions.assertEquals("seconduser", userList.get(1).getName());
    }

    @Test
    public void shouldAddFriendAndGetFriendListReturn() {
        User user1 = createFirstUser();
        User user2 = createSecondUser();
        userStorage.addFriendToUser(user1, user2);
        List<User> userList = userStorage.getFriendsList(user1);
        Assertions.assertTrue(userList.size() == 1);
        Assertions.assertEquals("seconduser", userList.get(0).getName(), "Неверный друг!");
    }

    @Test
    public void shoulFriendDelete() {
        User user1 = createFirstUser();
        User user2 = createSecondUser();
        userStorage.addFriendToUser(user1, user2);
        userStorage.deleteFriendFromUser(user1, user2);
        List<User> userList = userStorage.getFriendsList(user1);
        Assertions.assertTrue(userList.size() == 0, "Нет друзей у юзера 1");
    }

    @Test
    public void shouldFilmCreateAndGetFilmByIdWorking() {
        Film film = createFirstFilm();
        Optional<Film> optionalFilm = filmStorage.getFilmById(film.getId());
        assertThat(optionalFilm)
                .isPresent()
                .hasValueSatisfying(film2 ->
                        assertThat(film2).hasFieldOrPropertyWithValue("name", "Создан первый фильм")
                );
    }

    @Test
    public void shouldFilmUpdateAndGetFilmListIsWorking() {
        Film film = createFirstFilm();
        film.setName("Измененный фильм");
        filmStorage.updateFilm(film);
        List<Film> filmList = filmStorage.getFilmsList();

        Assertions.assertTrue(filmList.size() == 1, "Должен быть один фильма");
        Assertions.assertEquals("Измененный фильм", filmList.get(0).getName(), "Название фильма должно быть: Измененный фильма");
    }

    @Test
    public void shouldAddLikeOkAndGetPriorityIsWorking() {
        Film film1 = createFirstFilm();
        Film film2 = createSecondFilm();
        User user1 = createFirstUser();
        User user2 = createSecondUser();
        User user3 = createThirdUser();
        filmStorage.addLikeToFilm(film2, user1);
        filmStorage.addLikeToFilm(film2, user2);
        filmStorage.addLikeToFilm(film1, user3);
        List<Film> popularFilmList = filmStorage.getPrioritizedFilmList(10);

        Assertions.assertTrue(popularFilmList.size() == 2, "Должно  быть 2 фильма");
        Assertions.assertEquals("Создан первый фильм", popularFilmList.get(0).getName(), "Первый должен быть фильм 1");
    }

    @Test
    public void shouldDeleteLike() {
        Film film = createFirstFilm();
        User user1 = createFirstUser();
        User user2 = createSecondUser();
        User user3 = createThirdUser();
        filmStorage.addLikeToFilm(film, user1);
        filmStorage.addLikeToFilm(film, user2);
        filmStorage.addLikeToFilm(film, user3);
        filmStorage.deleteLike(film, user2);
        Optional<Film> optionalFilm = filmStorage.getFilmById(film.getId());
        Set<Integer> userList = optionalFilm.get().getLikes();

        Assertions.assertTrue(userList.size() == 2, "Лайков должно быть 2");
    }

    @Test
    public void shouldReturnGenre() {
        Optional<Genre> genre = genreStorage.getGenreById(1);

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(film2 ->
                        assertThat(film2).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void ShouldReturnAllGenres() {
        List<Genre> listGenre = genreStorage.getAllGenres();

        Assertions.assertEquals(6, listGenre.size(), "Всего 6 жанров!");
        Assertions.assertEquals("Комедия", listGenre.get(0).getName(), "Певрвой должны быть комедия");
        Assertions.assertEquals("Боевик", listGenre.get(5).getName(), "Ну а последним - боевик");
    }

    @Test
    public void ShouldReturnMpa() {
        Optional<Rating> mpa = mpaStorage.getRatingById(1);

        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(mpa1 ->
                        assertThat(mpa1).hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void ShouldReturnAllMpa() {
        List<Rating> listMpa = mpaStorage.getAllRatings();

        Assertions.assertEquals(5, listMpa.size(), "Всего 5 рейтинов!");
        Assertions.assertEquals("G", listMpa.get(0).getName(), "Первым должн быть рейтинг G");
        Assertions.assertEquals("NC-17", listMpa.get(4).getName(), "Ну а последним - NC-17");
    }

    private User createFirstUser() {
        User user = new User();
        //user.setId(1);
        user.setEmail("firstuser@mail.ru");
        user.setName("firstuser");
        user.setLogin("loginfirstuser");
        user.setBirthday(LocalDate.of(1981, 1, 1));
        return userStorage.createUser(user);
    }

    private User createSecondUser() {
        User user = new User();
        user.setEmail("seconduser@mail.ru");
        user.setName("seconduser");
        user.setLogin("loginseconduser");
        user.setBirthday(LocalDate.of(1982, 1, 1));
        return userStorage.createUser(user);
    }

    private User createThirdUser() {
        User user = new User();
        user.setEmail("thirduser@mail.ru");
        user.setName("thirduser");
        user.setLogin("loginthirduser");
        user.setBirthday(LocalDate.of(1983, 1, 1));
        return userStorage.createUser(user);
    }

    private Film createFirstFilm() {
        Film film = new Film();
        film.setName("Создан первый фильм");
        film.setDescription("о создании первого фильма");
        film.setReleaseDate(LocalDate.of(2022, 11, 24));
        film.setDuration(10);
        film.setMpa(mpaStorage.getRatingById(1).get());
        film.addGenre(genreStorage.getGenreById(1).get());
        return filmStorage.createFilm(film);
    }

    private Film createSecondFilm() {
        Film film = new Film();
        film.setName("Создан первый фильм");
        film.setDescription("о создании первого фильма");
        film.setReleaseDate(LocalDate.of(2022, 11, 24));
        film.setDuration(10);
        film.setMpa(mpaStorage.getRatingById(1).get());
        film.addGenre(genreStorage.getGenreById(1).get());
        return filmStorage.createFilm(film);
    }

}
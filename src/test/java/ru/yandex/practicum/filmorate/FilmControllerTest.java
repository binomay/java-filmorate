package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    //@Autowired
    //private PersonRepository repository;
    @Autowired
    private MockMvc mockMvc;
    private  final LocalDate minReleaseDate = LocalDate.of(1895,12,28);

    @Test
    public void shoulFilmNotCreateWithoutName() throws Exception {
        Film film = Film.builder()
                .description("говеный фильм")
                .duration(10)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shoulFilmNotCreateWithNegativeDuration() throws Exception {
        Film film = Film.builder()
                .name("Борзые перцы")
                .description("говеный фильм")
                .duration(-5)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shoulFilmNotCreateWithoutDescription() throws Exception {
        Film film = Film.builder()
                .name("Борзые перцы")
                .duration(-5)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shoulFilmNotCreateWithWrongDescription() throws Exception {
        StringBuilder longDescription = new StringBuilder();
        for (int i = 0; i < 300; i++){
            longDescription = longDescription.append("a");
        }
        Film film = Film.builder()
                .name("просто фильм")
                .description(longDescription.toString())
                .duration(20)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shoulFilmNotCreateWithWrongReleaseDate() throws Exception, ValidationException {
        Film film = Film.builder()
                .name("просто фильм")
                .description("описание фильма")
                .duration(20)
                .releaseDate(minReleaseDate.minusDays(1))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(x -> x.getResolvedException().getClass().equals(ValidationException.class));
    }
}

package ru.yandex.practicum.filmorate;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    //@Autowired
    //private PersonRepository repository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldUserCreateWithoutName() throws Exception {
        User user = User.builder()
                .login("user_login")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 10))
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("name").value("user_login"));
    }

    @Test
    public void shouldUserDoesntCreateWithoutLogin() throws Exception {
        User user = User.builder()
                .name("user_name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 10))
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldUserDoesntCreateWithFutureBitrhDate() throws Exception {
        User user = User.builder()
                .login("user_login")
                .name("user_name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(2055, 12, 10))
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldUserDoesntCreateWithIncorrectEmail() throws Exception {
        User user = User.builder()
                .login("user_login")
                .name("user_name")
                .email("user mail.ru")
                .birthday(LocalDate.of(2055, 12, 10))
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldUserDoesntCreateWithIncorrectLogin() throws Exception {
        User user = User.builder()
                .login("user login")
                .name("user_name")
                .email("user mail.ru")
                .birthday(LocalDate.of(1985, 12, 10))
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }
}
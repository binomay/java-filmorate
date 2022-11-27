package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@lombok.Data
public class Film {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    //@NotNull
    private LocalDate releaseDate;
    @Min(0)
    private int duration;
    private Set<Genre> genres = new HashSet<>();
    //рейтинг
    private Rating  mpa; //Андрей, ты дятел! mpa один!!!!!

    //пользовательские лайки
    private final Set<Integer> likes = new HashSet<>();

    public void setGenre(Genre genre){
        genres.add(genre);
    }
    public void setLike(int user_id) {
        likes.add(user_id);
    }

    public void deleteLike(User user) {
        likes.remove(user.getId());
    }

    public int giveCountLikes() {
        return likes.size();
    }
}

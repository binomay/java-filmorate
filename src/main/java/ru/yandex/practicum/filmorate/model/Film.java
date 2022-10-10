package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
    //пользовательские лайки
    private final Set<Integer> likes = new HashSet<>();

    public void setLike(User user) {
        likes.add(user.getId());
    }

    public void deleteLike(User user) {
        likes.remove(user.getId());
    }

    public int giveCountLikes() {
        return likes.size();
    }
}

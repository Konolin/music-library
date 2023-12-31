package map.project.musiclibrary.data.model.audios;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@MappedSuperclass
public abstract class Audio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id;

    @Column(name = "name")
    protected String name;

    @Column(name = "length")
    protected Integer length;

    @Column(name = "releaseDate")
    protected Date releaseDate;

    public void setReleaseDate(Date releaseDate) {
        if (releaseDate == null) {
            throw new IllegalArgumentException();
        }
        this.releaseDate = releaseDate;
    }
}


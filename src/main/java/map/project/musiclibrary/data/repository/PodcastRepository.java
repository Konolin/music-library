package map.project.musiclibrary.data.repository;

import map.project.musiclibrary.data.repository.model.Advertisement;
import map.project.musiclibrary.data.repository.model.HostUser;
import map.project.musiclibrary.data.repository.model.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PodcastRepository extends JpaRepository<Podcast, Long> {
    List<Podcast> findByName(String name);

    Optional<Podcast> findById(Long id);
}

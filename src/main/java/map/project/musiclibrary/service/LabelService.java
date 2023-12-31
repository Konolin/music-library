package map.project.musiclibrary.service;

import jakarta.persistence.EntityNotFoundException;
import map.project.musiclibrary.data.model.misc.Label;
import map.project.musiclibrary.data.model.users.ArtistUser;
import map.project.musiclibrary.data.model.users.UserSession;
import map.project.musiclibrary.data.repository.ArtistUserRepository;
import map.project.musiclibrary.data.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LabelService {
    private final LabelRepository labelRepository;
    private final ArtistUserRepository artistUserRepository;

    @Autowired
    public LabelService(LabelRepository labelRepository, ArtistUserRepository artistUserRepository) {
        this.labelRepository = labelRepository;
        this.artistUserRepository = artistUserRepository;
    }

    public Label addLabel(String name) {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser().isAdmin()) {
            Label label = new Label();
            label.setName(name);
            label.setArtists(new ArrayList<>());
            return labelRepository.save(label);
        }
        throw new SecurityException("Only admin can add labels");
    }

    public void deleteLabel(String idStr) throws NumberFormatException {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser().isAdmin()) {
            Long id = Long.parseLong(idStr);
            Optional<Label> labelOptional = labelRepository.findById(id);

            if (labelOptional.isPresent()) {
                Label label = labelOptional.get();
                for (ArtistUser artistUser : label.getArtists()) {
                    artistUser.setLabel(null);
                    artistUserRepository.save(artistUser);
                }
                label.getArtists().clear();
                labelRepository.deleteById(id);
            }
            throw new EntityNotFoundException("LabelService::Label with specified id doesn't exist");
        }
        throw new SecurityException("Only admin can delete labels");
    }

    public Label save(Label label) {
        return labelRepository.save(label);
    }

    public Label findByName(String name) {
        return labelRepository.findByName(name).stream().findFirst().orElse(null);
    }

    public List<Label> findAll() {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser().isAdmin()) {
            return labelRepository.findAll();
        }
        throw new SecurityException("Only admin can list all labels");
    }

    public Label addArtist(String artistIdStr, String labelIdStr) throws NumberFormatException {
        if (UserSession.isLoggedIn() && UserSession.getCurrentUser().isAdmin()) {
            Long artistId = Long.parseLong(artistIdStr);
            Long labelId = Long.parseLong(labelIdStr);

            // search for the artist and label with the corresponding ids
            Optional<ArtistUser> artistUserOptional = artistUserRepository.findById(artistId);
            Optional<Label> labelOptional = labelRepository.findById(labelId);

            if (artistUserOptional.isPresent() && labelOptional.isPresent()) {
                // get the artist and label with the corresponding ids
                ArtistUser artistUser = artistUserOptional.get();
                Label label = labelOptional.get();
                // add artist to the labels list of artists
                label.addArtist(artistUser);
                artistUser.setLabel(label);
                artistUserRepository.save(artistUser);
                // update the label in repo
                return labelRepository.save(label);
            }

            throw new EntityNotFoundException("LabelService::Artist or label with specified id doesn't exist");
        }
        throw new SecurityException("Only admin can add artists to labels");
    }
}

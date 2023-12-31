package map.project.musiclibrary.ui.rest;

import jakarta.persistence.EntityNotFoundException;
import map.project.musiclibrary.data.dto.AlbumDTO;
import map.project.musiclibrary.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/album")
public class AlbumEndpoint {
    private final AlbumService albumService;

    @Autowired
    public AlbumEndpoint(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/list")
    public String listAlbums() {
        try {
            return albumService.findAll().toString();
        } catch (SecurityException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/add")
    public String addAlbum(@RequestBody AlbumDTO request) {
        try {
            return albumService.addAlbum(request.getName(), request.getArtistId(), request.getSongIds()).toString();
        } catch (NumberFormatException e) {
            return "Error: Invalid integer format. Please provide valid numbers for song IDs.";
        } catch (IllegalArgumentException | SecurityException e) {
            return e.getMessage();
        }
    }

    @GetMapping("/find")
    public String findAlbum(@RequestParam String name) {
        return albumService.findByName(name).toString();
    }

    @DeleteMapping("/delete")
    public String deleteAlbum(@RequestParam String idStr) {
        try {
            albumService.delete(idStr);
            return "Album with ID " + idStr + " has been deleted successfully!";
        } catch (NumberFormatException e) {
            return "Error: Invalid integer format. Please provide a valid number.";
        } catch (EntityNotFoundException | SecurityException e) {
            return e.getMessage();
        }
    }
}

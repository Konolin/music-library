package map.project.musiclibrary.ui.rest;

import jakarta.persistence.EntityNotFoundException;
import map.project.musiclibrary.data.dto.HostDTO;
import map.project.musiclibrary.service.HostUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/host")
public class HostEndpoint {
    private final HostUserService hostUserService;

    @Autowired
    public HostEndpoint(HostUserService hostUserService) {
        this.hostUserService = hostUserService;
    }

    @GetMapping("/list")
    public String listHostUsers() {
        try {
            return hostUserService.findAll().toString();
        } catch (SecurityException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/add")
    public String addHost(@RequestBody HostDTO request) {
        try {
            return hostUserService.addHost(request.getName(), request.getBirthdate()).toString();
        } catch (SecurityException | IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteHost(@RequestParam String idStr) {
        try {
            hostUserService.deleteHost(idStr);
            return "Host with ID " + idStr + " has been deleted successfully!";
        } catch (NumberFormatException e) {
            return "Invalid id format";
        } catch (EntityNotFoundException | SecurityException e) {
            return e.getMessage();
        }
    }

    @GetMapping("/find")
    public String findHost(@RequestParam String name) {
        return hostUserService.findByName(name).toString();
    }

    @GetMapping("listPodcasts")
    public String listPodcasts(@RequestParam String idStr) {
        try {
            return hostUserService.listHostsPodcasts(idStr).toString();
        } catch (NumberFormatException e) {
            return "Error: Invalid integer format. Please provide a valid number.";
        } catch (EntityNotFoundException e) {
            return e.getMessage();
        }
    }
}

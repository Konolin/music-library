package map.project.musiclibrary.service;

import map.project.musiclibrary.data.model.users.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminService {
    public Admin login(String email, String password) {
        if ("admin".equals(email) && "admin".equals(password)) {
            return Admin.getInstance();
        }
        return null;
    }
}

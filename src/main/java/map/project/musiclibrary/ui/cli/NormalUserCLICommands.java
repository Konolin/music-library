package map.project.musiclibrary.ui.cli;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import map.project.musiclibrary.data.model.misc.Notification;
import map.project.musiclibrary.data.model.users.Admin;
import map.project.musiclibrary.data.model.users.NormalUser;
import map.project.musiclibrary.data.model.users.UserSession;
import map.project.musiclibrary.service.NormalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ShellComponent
public class NormalUserCLICommands {
    private final NormalUserService normalUserService;
    private final UserSession userSession;

    @Autowired
    public NormalUserCLICommands(NormalUserService normalUserService, UserSession userSession) {
        this.normalUserService = normalUserService;
        this.userSession = userSession;
    }

    @ShellMethod(key = "listUsers", value = "List all users")
    public String listUsers() {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin) {
            return normalUserService.findAll().toString();
        } else {
            return "Only admin can list all users.";
        }
    }

    @ShellMethod(key = "addUser", value = "Add a user")
    public String addUser(@ShellOption(value = {"name"}, help = "Name of the user") final String name,
                          @ShellOption(value = {"email"}, help = "Email of the user") final String email,
                          @ShellOption(value = {"password"}, help = "Password of the user") final String password,
                          @ShellOption(value = {"birthdate"}, help = "Birthdate of the user (yyyy-MM-dd)") final String birthdateStr,
                          @ShellOption(value = {"isPremium"}, help = "Is the user premium (boolean)") final String isPremiumStr) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin) {
            try {
                return normalUserService.addNormalUser(name, email, password, isPremiumStr, birthdateStr).toString();
            } catch (ParseException e) {
                return "Error: Invalid birthdate format. Please use yyyy-MM-dd.";
            } catch (EntityExistsException e) {
                return "Error: Email already in use.";
            } catch (IllegalArgumentException e) {
                return "Error: Email can not be set to admin.";
            }
        } else {
            return "Only admin can add users.";
        }
    }

    @ShellMethod(key = "deleteUser", value = "Delete a user")
    public String deleteUser(@ShellOption(value = {"userId"}, help = "Id of the user to be deleted") final String userIdstr){
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin){
            try{
                Long userId = Long.parseLong(userIdstr);
                normalUserService.deleteNormalUser(userId);
                return "User with ID " + userId + " has been deleted.";
            } catch (IllegalArgumentException e){
                return "Error: Invalid integer format. Please provide a valid number.";
            }
        } else {
            return "Only admin can delete a user.";
        }
    }

    @ShellMethod(key = "updateUser", value = "Update user attributes")
    public String updateUser(@ShellOption(value = {"password"}, help = "Update user password") final boolean updatePassword,
                             @ShellOption(value = {"isPremium"}, help = "Update subscription plan") final boolean updatePremium) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser) {
            try {
                Long id = userSession.getCurrentUser().getId();
                Map<String, Object> updates = new HashMap<>();

                if (updatePassword) {
                    updates.put("password", true);
                }

                if (updatePremium) {
                    updates.put("isPremium", true);
                }

                return normalUserService.updateUser(id, updates);

            } catch (NumberFormatException e) {
                return "Error: Invalid user ID format. Please provide a valid number.";
            }
        } else {
            return "Only normal users can modify their password/premium status";
        }
    }






    @ShellMethod(key = "findUser", value = "Find a user by name")
    public String findUser(@ShellOption(value = {"name"}, help = "Name of the user") final String name) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin) {
            return normalUserService.findByName(name).toString();
        } else {
            return "Only admin can search for users.";
        }
    }

    @ShellMethod(key = "login", value = "Log in as a NormalUser")
    public String login(@ShellOption(value = {"email"}, help = "Email of the user") final String email,
                        @ShellOption(value = {"password"}, help = "Password of the user") final String password) {
        NormalUser user = normalUserService.login(email, password);
        if (user != null) {
            userSession.login(user);
            return "Login successful. Welcome, " + userSession.getCurrentUser().getName();
        } else {
            return "Invalid credentials. Please try again.";
        }
    }

    @ShellMethod(key = "logout", value = "Log out the current user")
    public String logout() {
        if (userSession.isLoggedIn()) {
            String goodbyeMessage = "Logout successful. Goodbye " + userSession.getCurrentUser().getName();
            userSession.logout();
            return goodbyeMessage;
        } else {
            return "No user is currently logged in.";
        }
    }


    @ShellMethod(key = "currentUser", value = "Get the current user that is logged in")
    public String getCurrentUser() {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin) {
            return this.userSession.getCurrentUser().toString();
        } else if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser) {
            return this.userSession.getCurrentUser().toString();
        } else {
            return "No user is currently logged in.";
        }
    }

    @ShellMethod(key = "followArtist", value = "Follow an artist")
    public String followArtist(@ShellOption(value = {"artistId"}, help = "ID of the artist") final String artistIdStr) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser) {
            try {
                Long artistId = Long.parseLong(artistIdStr);
                normalUserService.followArtist((NormalUser) userSession.getCurrentUser(), artistId);
                return "You are now following the artist with ID " + artistId;
            } catch (NumberFormatException e) {
                return "Error: Invalid integer format. Please provide a valid number.";
            }
        } else {
            return "Only normal users can follow artists.";
        }
    }

    @ShellMethod(key = "unfollowArtist", value = "Unfollow an artist")
    public String unfollowArtist(@ShellOption(value = {"artistId"}, help = "ID of the artist") final String artistIdStr) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser) {
            try {
                normalUserService.unfollowArtist((NormalUser) userSession.getCurrentUser(), artistIdStr);
                return "You have unfollowed the artist with ID " + artistIdStr;
            } catch (NumberFormatException e) {
                return "Error: Invalid integer format. Please provide a valid number.";
            } catch (EntityNotFoundException e) {
                return "Artist or user not found.";
            }
        } else {
            return "Only normal users can unfollow artists.";
        }
    }

    @ShellMethod(key = "seeNewNotifications", value = "See new notifications")
    public String seeNewNotifications() {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser currentUser) {
            List<Notification> notifications = normalUserService.getNotifications(currentUser);
            return (!notifications.isEmpty()) ? notifications.toString() : "No new notifications";
        } else {
            return "Only logged-in normal users can see notifications.";
        }
    }
}
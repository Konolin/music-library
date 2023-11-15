package map.project.musiclibrary.cli;

import map.project.musiclibrary.data.model.Admin;
import map.project.musiclibrary.data.model.LoginCredentials;
import map.project.musiclibrary.data.model.NormalUser;
import map.project.musiclibrary.data.model.UserSession;
import map.project.musiclibrary.service.NormalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            throw new RuntimeException("Only admin can list all users");
        }
    }

    @ShellMethod(key = "addUser", value = "Add a user")
    public String addUser(@ShellOption(value = {"name"}, help = "Name of the user") final String name,
                          @ShellOption(value = {"email"}, help = "Email of the user") final String email,
                          @ShellOption(value = {"password"}, help = "Password of the user") final String password,
                          @ShellOption(value = {"birthdate"}, help = "Birthdate of the user (yyyy-MM-dd)") final String birthdateString,
                          @ShellOption(value = {"isPremium"}, help = "Is the user premium (boolean)") final String isPremiumStr) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin) {
            NormalUser user = new NormalUser();
            user.setName(name);

            LoginCredentials loginCredentials = new LoginCredentials();
            loginCredentials.setEmail(email);  // TODO - email unic sau cauta logincredential daca mai exista si thorw exception
            loginCredentials.setPassword(password);

            user.setLoginCredentials(loginCredentials);
            loginCredentials.setUser(user);

            try {
                boolean isPremium = Boolean.parseBoolean(isPremiumStr);
                user.setPremium(isPremium);
            } catch (NumberFormatException e) {
                return "Error: Invalid boolean string";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date birthdate = dateFormat.parse(birthdateString);
                user.setBirthdate(birthdate);
            } catch (ParseException e) {
                return "Error: Invalid birthdate format. Please use yyyy-MM-dd.";
            }
            return normalUserService.save(user).toString();
        } else {
            return "Only admin can add users";
        }
    }

    @ShellMethod(key = "findUser", value = "Find a user by name")
    public String findUser(@ShellOption(value = {"name"}, help = "Name of the user") final String name) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin) {
            return normalUserService.findByName(name).toString();
        } else {
            throw new RuntimeException("Only admin can search for users");
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
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin)
            return (this.userSession.getCurrentUser()).toString();
        else if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser) {
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
            throw new RuntimeException("Only normal users can follow artists");
        }
    }

    @ShellMethod(key = "unfollowArtist", value = "Unfollow an artist")
    public String unfollowArtist(@ShellOption(value = {"artistId"}, help = "ID of the artist") final String artistIdStr) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser) {
            try {
                Long artistId = Long.parseLong(artistIdStr);
                normalUserService.unfollowArtist((NormalUser) userSession.getCurrentUser(), artistId);
                return "You have unfollowed the artist with ID " + artistId;
            } catch (NumberFormatException e) {
                return "Error: Invalid integer format. Please provide a valid number.";
            }
        } else {
            throw new RuntimeException("Only normal users can unfollow artists");
        }
    }

    @ShellMethod(key = "seeNewNotifications", value = "See new notifications")
    public String seeNewNotifications() {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser currentUser) {
            return currentUser.seeNewNotifications();
        } else {
            return "Only logged-in normal users can see notifications.";
        }
    }




}

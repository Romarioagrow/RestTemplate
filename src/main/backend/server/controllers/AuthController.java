package server.controllers;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import server.domain.User;
import server.services.UserService;
import java.util.Map;

@Log
@RestController
@AllArgsConstructor
//@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;


    @GetMapping("/auth")
    public User authUser(@AuthenticationPrincipal User user) {
        return user;
    }

    @PostMapping("/user/registration")
    private boolean registration(@RequestBody Map<String, String> userDetails) {
        log.info("user registration");
        log.info(userDetails.toString());
        return userService.registerUser(userDetails);
    }

    @PostMapping("/auth/noUser")
    private boolean noUser(@AuthenticationPrincipal User user) {
        //log.info("Has user: " + (user == null));
        return user == null;
    }

    @PostMapping("/auth/hasUser")
    private boolean hasUser(@AuthenticationPrincipal User user) {
        //log.info("Has user: " + (user == null));
        return user != null;
    }
}

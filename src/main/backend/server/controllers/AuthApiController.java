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
@RequestMapping("/auth")
public class AuthApiController {
    private final UserService userService;

    @PostMapping
    public User authUser(@AuthenticationPrincipal User user) {
        return user;
    }

    @PostMapping("/user/registration")
    private boolean registration(@RequestBody Map<String, String> userDetails) {
        return userService.registerUser(userDetails);
    }

    @PostMapping("/noUser")
    private boolean noUser(@AuthenticationPrincipal User user) {
        return user == null;
    }

    @PostMapping("hasUser")
    private boolean hasUser(@AuthenticationPrincipal User user) {
        return user != null;
    }
}

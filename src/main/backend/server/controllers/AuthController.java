package server.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.domain.User;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @GetMapping
    public User authUser(@AuthenticationPrincipal User user) {
        return user;
    }
}

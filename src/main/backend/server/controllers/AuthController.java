package server.controllers;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import server.domain.User;
import java.util.HashMap;

@Controller
//@RequestMapping("/user")
public class AuthController {
    /*@GetMapping
    public String auth(Model model, @AuthenticationPrincipal User user) {
        HashMap<Object, Object> data = new HashMap<>();
        data.put("profile", user);
        model.addAttribute("frontendData", data);
        return "static/index.html";
    }*/
}


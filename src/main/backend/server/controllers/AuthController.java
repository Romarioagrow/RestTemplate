package server.controllers;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import server.domain.User;
import server.repos.UserRepo;
import server.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Log
@Controller
//@RequestMapping("/user")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    /*@GetMapping
    public String auth(Model model, @AuthenticationPrincipal User user) {
        HashMap<Object, Object> data = new HashMap<>();
        data.put("profile", user);
        model.addAttribute("frontendData", data);
        return "static/index.html";
    }*/

    /*@PostMapping("/login")
    public UserDetails login(@RequestBody Map<String, String> userCredentials, HttpServletRequest request) {
        log.info("userName controller: " + userCredentials.get("username"));
        log.info("request: "+request.toString());
        log.info(request.getPathInfo());
        return userService.loadUserByUsername(userCredentials.get("username"));
    }*/




    //@ApiOperation(value = "Login")
    //@RequestMapping(value = "/login", method = RequestMethod.POST)
    /*@PostMapping("/login")
    public boolean login(@RequestBody Map<String, String> user, HttpServletRequest request) {

        //UserDetails = userService.loadUserByUsername(user, request);

        String username = user.get("username");
        String password = user.get("password");


        //User u = this.userService.login(username, password);
        if (userService.login(username, password) != null) {
            log.info("Logging user");

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // Create a new session and add the security context.
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            log.info("LOGIN USER");
            return true;
        }
        log.warning("login FAILED");
        return false;
    }*/


    /*@PostMapping("/login")
    public boolean login(@RequestBody Map<String, String> user, HttpServletRequest request) {
        log.info("map: "+user.toString());

        String username = user.get("username");
        String password = user.get("password");


        User userCheck = userRepo.findByUsername(username);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        boolean passwordsMatch = passwordEncoder.matches(authRequest.getCredentials().toString(), passwordEncoder.encode(password));
        log.info("passwordsMatch: " + passwordsMatch);



        //log.info(username);
        //log.info(password);
        //log.info(password);

        *//*User u = userService.login(username, password);

        log.info(u.getUsername());

        if (u != null) {


            log.info(authRequest.getCredentials().toString());

            // Authenticate the user
            *//**//*Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // Create a new session and add the security context.
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            return true;*//**//*
        }*//*
        return false;
    }*/
}


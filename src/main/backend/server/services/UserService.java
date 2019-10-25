package server.services;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import server.domain.User;
import server.domain.categories.Role;
import server.repos.UserRepo;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Log
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private static final String SPRING_SECURITY_CONTEXT_KEY = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: " + username);
        return userRepo.findByUsername(username);
    }

    public boolean registerUser(Map<String, String> userDetails) {

        if (userRepo.findByUsername(userDetails.get("username")) != null) return false;

        User user = new User();
        user.setPassword(passwordEncoder.encode(userDetails.get("password")));
        user.setUsername(userDetails.get("username"));
        user.setFirstName(userDetails.get("firstName"));
        user.setLastName(userDetails.get("lastName"));
        user.setEmail(userDetails.get("email"));

        user.setActive(true);
        user.setRegistrationDate(LocalDateTime.now());
        user.setRoles(Collections.singleton(Role.USER));
        userRepo.save(user);



        /*user.setFirstName(user.getFirstName().trim());
        user.setLastName(user.getLastName().trim());
        user.setPatronymic(user.getPatronymic().trim());*/

        log.info(user.toString());
        return true;
    }

    public User login(String account, String password) {
        User user = userRepo.findByUsername(account);
        if (user != null) {
            log.info("userPass: "+user.getPassword());
            log.info("password: "+password);
            if (this.passwordEncoder.matches(password, "$2a$08$t06C6m2Nn4qv4IRGzZ.ct.7.04Yg8pTzmNBb7KK657qrBqpBXo2Oq") && user.isEnabled()) {
                log.info("return USER account:" + account);
                return user;
            }
        }
        log.warning("return NULL for account: "+account);
        return null;
    }

    /*public User login(String username, String password) {
        //passwordEncoder.matches(userRepo.findByUsername(username).getPassword(), password);
        *//*User user = userRepo.findByUsername(username);
        log.info(user.getPassword());

        //log.info(passwordEncoder.matches(user.getPassword(), passwordEncoder.encode(password)) + "");

        boolean checkUser = userRepo.findByUsername(username) != null && passwordEncoder.matches(user.getPassword(), password);
        return checkUser ? userRepo.findByUsername(username) : null;*//*
        return null;
    }*/
}

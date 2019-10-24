package server.services;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import server.domain.User;
import server.domain.categories.Role;
import server.repos.UserRepo;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Log
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUser: "+username);
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
}

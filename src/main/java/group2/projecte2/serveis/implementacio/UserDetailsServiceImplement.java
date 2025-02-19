package group2.projecte2.serveis.implementacio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import group2.projecte2.model.User;
import group2.projecte2.repositori.jpa.UserRepository;
import group2.projecte2.serveis.UserDetailsServices;

public class UserDetailsServiceImplement implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        return new UserDetailsServices(user);
    }

}

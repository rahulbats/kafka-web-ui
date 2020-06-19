package kafka.service;

import kafka.model.UsersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UsersBean usersBean;


    @Value("${LDAP_ENABLED:false}")
    private boolean ldapEnabled;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


            if (usersBean.getPassword(username)!=null) {
                return new User(username, new BCryptPasswordEncoder().encode( usersBean.getPassword(username)),
                        new ArrayList<>());
            } else {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

    }
}
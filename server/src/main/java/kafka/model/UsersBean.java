package kafka.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class UsersBean {

    @Value("${allowedusers}")
    private String allowedUsers;

    Map<String, String> userPasswords = new HashMap<>();

    @PostConstruct
    public void init() {
        String[] userPasswordPairs = allowedUsers.split(",");
        for(String userPasswordPair: userPasswordPairs) {
            String[] userPassword = userPasswordPair.split(":");
            userPasswords.put(userPassword[0], userPassword[1]);
        }
    }

    public String getPassword(String username) {
        return userPasswords.get(username);
    }
}

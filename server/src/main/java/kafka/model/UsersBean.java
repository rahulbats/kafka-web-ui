package kafka.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class UsersBean {

    @Value("${allowedusers:#{null}}")
    private String allowedUsers;

    @Value("${allowedusersProp:#{null}}")
    private String allowedUsersProp;

    //Map<String, String> userPasswords = new HashMap<>();
    Properties userProps = new Properties();
    @PostConstruct
    public void init() throws IOException {
        if(allowedUsersProp!=null) {
            InputStream inputStream  = new FileInputStream(allowedUsersProp.trim());
            userProps.load(inputStream);
        } else {
            String[] userPasswordPairs = allowedUsers.split(",");
            for(String userPasswordPair: userPasswordPairs) {
                String[] userPassword = userPasswordPair.split(":");
                userProps.put(userPassword[0], userPassword[1]);
                //userPasswords.put(userPassword[0], userPassword[1]);
            }
        }


    }

    public String getPassword(String username) {

        return userProps.get(username).toString();
    }
}

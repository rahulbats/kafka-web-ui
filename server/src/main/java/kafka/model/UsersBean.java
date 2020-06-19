package kafka.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.naming.*;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UsersBean {

    @Value("${allowedusers:#{null}}")
    private String allowedUsers;

    @Value("${allowedusersProp:#{null}}")
    private String allowedUsersProp;


    @Value("${ldap.enabled:false}")
    private boolean ldapEnabled;

    @Value("${ldap.role.prefix:}")
    private String ldapRolePrefix;

    //Map<String, String> userPasswords = new HashMap<>();
    Properties userProps = new Properties();
    DirContext context = null;
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

    public String getPassword(String username)  {

        return userProps.get(username).toString();
    }

    public List<String> getMappedSASLUser(List<SimpleGrantedAuthority> authorities)  {
        int prefixLength=ldapRolePrefix.length();
        return authorities.stream().map(authority->authority.toString()).filter(role->role.indexOf(ldapRolePrefix.toLowerCase())>-1 && userProps.get(role.substring(prefixLength))!=null).map(authority->authority.substring(prefixLength)).collect(Collectors.toList());

    }

    private SearchControls getSimpleSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(30000);
        //String[] attrIDs = {"objectGUID"};
        //searchControls.setReturningAttributes(attrIDs);
        return searchControls;
    }
}

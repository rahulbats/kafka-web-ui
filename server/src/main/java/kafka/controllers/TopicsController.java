package kafka.controllers;

import kafka.model.Topic;
import kafka.model.UsersBean;
import kafka.service.KafkaTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NamingException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TopicsController {

    @Autowired
    private KafkaTopicService kafkaTopicService;

    @Autowired
    private UsersBean usersBean;


    @Value("${LDAP_ENABLED:false}")
    private boolean ldapEnabled;

    @GetMapping(path ="/topics", produces = "application/json")
    public List<Topic> getTopics() throws NamingException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        if(ldapEnabled) {
            Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) ((UserDetails)principal).getAuthorities();
            List<String> usernames = usersBean.getMappedSASLUser(authorities.stream().map(authority->authority).collect(Collectors.toList()));
            Map<String, Topic> unionedTopics = new HashMap<>();
            usernames.forEach(SASLusername->{
              List<Topic> topics = kafkaTopicService.listTopics(SASLusername, usersBean.getPassword(SASLusername));
              topics.forEach(topic -> unionedTopics.put(topic.getName(), topic));
            });
            List<Topic> unionedTopicsList = new ArrayList<Topic>( unionedTopics.values());
            unionedTopicsList.sort(new Comparator<Topic>() {
                @Override
                public int compare(Topic o1, Topic o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            return unionedTopicsList;
        }

        return kafkaTopicService.listTopics(username, usersBean.getPassword(username));
    }

}

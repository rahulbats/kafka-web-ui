package kafka.controllers;

import kafka.model.Topic;
import kafka.model.UsersBean;
import kafka.service.KafkaTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
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

    @PostMapping(path ="/topics", produces = "application/json")
    public void createTopic(@RequestBody Topic topic) throws NamingException, InterruptedException, ExecutionException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        if(ldapEnabled) {
            Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) ((UserDetails)principal).getAuthorities();
            List<String> usernames = usersBean.getMappedSASLUser(authorities.stream().map(authority->authority).collect(Collectors.toList()));

            boolean topicCreated = false;
            ExecutionException exception = null;
            for(String adGroupname: usernames) {
               try{
                   kafkaTopicService.createTopics(adGroupname, usersBean.getPassword(adGroupname), topic);
               } catch (ExecutionException ex) {
                   exception = ex;
                   continue;
               }
               topicCreated = true;
               break;

            }
            if(!topicCreated){
                throw exception;
            }


            //return topicCreated;
        }

        kafkaTopicService.createTopics(username, usersBean.getPassword(username), topic);
    }

    @DeleteMapping(path ="/topics/{topicName}", produces = "application/json")
    public void deleteTopic(@PathVariable String topicName) throws NamingException, InterruptedException, ExecutionException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        kafkaTopicService.deleteTopic(username, usersBean.getPassword(username), topicName);
    }
}

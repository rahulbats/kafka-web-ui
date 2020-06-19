package kafka.controllers;

import kafka.model.*;
import kafka.service.KafkaMessageService;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NamingException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
public class MessagesController {
    @Autowired
    KafkaMessageService kafkaMessageService;

    @Autowired
    private UsersBean usersBean;


    @Value("${LDAP_ENABLED:false}")
    private boolean ldapEnabled;

    @GetMapping(path ="/messages/{topic}/{partition}/{maxMessagesToReturn}/{hideOlderMessages}", produces = "application/json")
    public MessagesContainer getMessages(@PathVariable String topic, @PathVariable int partition, @PathVariable int maxMessagesToReturn, @PathVariable boolean hideOlderMessages) throws ExecutionException, InterruptedException, NamingException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        if(ldapEnabled) {
            Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) ((UserDetails)principal).getAuthorities();
            List<String> saslUsers = usersBean.getMappedSASLUser(authorities.stream().map(authority->authority).collect(Collectors.toList()));
            username = saslUsers.get(0);
            MessagesContainer messagesContainer = null;
            for(String saslUser:saslUsers) {
                try {
                    messagesContainer = kafkaMessageService.listMessages( saslUser,  usersBean.getPassword(saslUser),  topic,  partition ,
                            maxMessagesToReturn, hideOlderMessages);
                    break;
                } catch (ExecutionException e) {
                    continue;
                } catch (InterruptedException e) {
                    continue;
                } catch (TopicAuthorizationException e) {
                    continue;
                }
            }
            return messagesContainer;
        }
        return kafkaMessageService.listMessages( username,  usersBean.getPassword(username),  topic,  partition ,
                maxMessagesToReturn, hideOlderMessages);
    }

}

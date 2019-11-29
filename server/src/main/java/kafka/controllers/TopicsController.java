package kafka.controllers;

import kafka.model.Topic;
import kafka.model.UsersBean;
import kafka.service.KafkaTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TopicsController {

    @Autowired
    private KafkaTopicService kafkaTopicService;

    @Autowired
    private UsersBean usersBean;

    @GetMapping(path ="/topics", produces = "application/json")
    public List<Topic> getTopics() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();

        return kafkaTopicService.listTopics(username, usersBean.getPassword(username));
    }

}

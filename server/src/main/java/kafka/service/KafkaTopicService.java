package kafka.service;

import kafka.model.Topic;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class KafkaTopicService {
    @Value("${security_protocol}")
    private String security_protocol;


    @Value("${bootstrap_servers}")
    private String bootstrap_servers;

    public List<Topic> listTopics(String username, String password) throws KafkaException{
        Map<String, List<PartitionInfo>> topics;

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrap_servers);
        props.put("group.id", "test-consumer-group");
        props.put("security.protocol", security_protocol);
        props.put("sasl.mechanism","PLAIN");
        props.put("group.id", "test-consumer-group");
        props.put("sasl.jaas.config","org.apache.kafka.common.security.plain.PlainLoginModule required  username=\""+username+"\"  password=\""+password+"\";");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        topics = consumer.listTopics();
        consumer.close();
        return topics.keySet().stream().map(topicName-> new Topic(topicName, topics.get(topicName).size())).collect(Collectors.toList());

    }
}

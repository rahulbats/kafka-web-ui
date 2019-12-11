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


    @Value("${ssl.truststore.location}")
    private String truststoreLocation;

    @Value("${ssl.keystore.location}")
    private String keystoreLocation;

    @Value("${ssl.truststore.password}")
    private String truststorePassword;

    @Value("${ssl.truststore.credentials}")
    private String truststoreCredentials;

    @Value("${ssl.keystore.password}")
    private String keystorePassword;

    @Value("${ssl.key.password}")
    private String keyPassword;

    @Value("${sasl.mechanism}")
    private String saslMechanism;

    public List<Topic> listTopics(String username, String password) throws KafkaException{
        Map<String, List<PartitionInfo>> topics;

        String jaasString = saslMechanism.equals("PLAIN")?
                "org.apache.kafka.common.security.plain.PlainLoginModule required  username=\""+username+"\"  password=\""+password+"\";":
                "org.apache.kafka.common.security.scram.ScramLoginModule required  username=\""+username+"\"  password=\""+password+"\";";



        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrap_servers);
        props.put("group.id", "kafka-ui-consumer-group");
        props.put("security.protocol", security_protocol);
        props.put("sasl.mechanism",saslMechanism);
        props.put("sasl.jaas.config",jaasString);
        if(truststoreLocation!=null)
            props.put("ssl.truststore.location", truststoreLocation);
        if(keystoreLocation!=null)
            props.put("ssl.keystore.location", keystoreLocation);
        if(truststorePassword!=null)
            props.put("ssl.truststore.password", truststorePassword);
        if(truststoreCredentials!=null)
            props.put("ssl.truststore.credentials", truststoreCredentials);
        if(keystorePassword!=null)
            props.put("ssl.keystore.password", keystorePassword);
        if(keyPassword!=null)
            props.put("ssl.key.password", keyPassword);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        topics = consumer.listTopics();
        consumer.close();
        return topics.keySet().stream().map(topicName-> new Topic(topicName, topics.get(topicName).size())).collect(Collectors.toList());

    }
}

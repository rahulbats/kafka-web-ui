package kafka.service;

import kafka.model.Topic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    final Log logger = LogFactory.getLog(getClass());
    @Value("${security_protocol}")
    private String security_protocol;


    @Value("${bootstrap_servers}")
    private String bootstrap_servers;


    @Value("${ssl.truststore.location:#{null}}")
    private String truststoreLocation;

    @Value("${ssl.keystore.location:#{null}}")
    private String keystoreLocation;

    @Value("${ssl.truststore.password:#{null}}")
    private String truststorePassword;

    @Value("${ssl.truststore.credentials:#{null}}")
    private String truststoreCredentials;

    @Value("${ssl.keystore.password:#{null}}")
    private String keystorePassword;

    @Value("${ssl.key.password:#{null}}")
    private String keyPassword;

    @Value("${sasl.mechanism}")
    private String saslMechanism;

    public List<Topic> listTopics(String username, String password) throws KafkaException{
        Map<String, List<PartitionInfo>> topics;

        String jaasString = saslMechanism.equals("PLAIN")?
                "org.apache.kafka.common.security.plain.PlainLoginModule required  username=\""+username+"\"  password=\""+password+"\";":
                "org.apache.kafka.common.security.scram.ScramLoginModule required  username=\""+username+"\"  password=\""+password+"\";";



        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrap_servers.trim());
        props.put("group.id", "kafka-ui-consumer-group");
        props.put("security.protocol", security_protocol.trim());
        props.put("sasl.mechanism",saslMechanism.trim());
        props.put("sasl.jaas.config",jaasString.trim());
        if(truststoreLocation!=null)
            props.put("ssl.truststore.location", truststoreLocation.trim());
        if(keystoreLocation!=null)
            props.put("ssl.keystore.location", keystoreLocation.trim());
        if(truststorePassword!=null)
            props.put("ssl.truststore.password", truststorePassword.trim());
        if(truststoreCredentials!=null)
            props.put("ssl.truststore.credentials", truststoreCredentials.trim());
        if(keystorePassword!=null)
            props.put("ssl.keystore.password", keystorePassword.trim());
        if(keyPassword!=null)
            props.put("ssl.key.password", keyPassword.trim());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        topics = consumer.listTopics();
        consumer.close();
        return topics.keySet().stream().map(topicName-> new Topic(topicName, topics.get(topicName).size())).collect(Collectors.toList());

    }
}

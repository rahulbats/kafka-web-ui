package kafka.service;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import kafka.model.Message;
import kafka.model.MessageType;
import kafka.model.Topic;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.Records;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KafkaMessageService {

    @Value("${security_protocol}")
    private String security_protocol;


    @Value("${bootstrap_servers}")
    private String bootstrap_servers;

    @Value("${schema.registry.url}")
    private String schemaRegistryURL;

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



    public List<Message> listMessages(String username, String password, String topic, int partition,
                                      int maxMessagesToReturn) throws KafkaException {
        Map<String, List<PartitionInfo>> topics;
        MessageType keyType;
        MessageType valueType;

        try {
            URL url = new URL(schemaRegistryURL+"/subjects/"+topic+"-key/versions/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if(status!=200)
                keyType =  MessageType.valueOf("JSON");
            else
                keyType =  MessageType.valueOf("AVRO");
        } catch (Exception e){
            keyType =  MessageType.valueOf("JSON");
        }


        try {
            URL url = new URL(schemaRegistryURL+"/subjects/"+topic+"-value/versions/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if(status!=200)
                valueType =  MessageType.valueOf("JSON");
            else
                valueType =  MessageType.valueOf("AVRO");
        } catch (Exception e){
            valueType =  MessageType.valueOf("JSON");
        }

        String jaasString = saslMechanism.equals("PLAIN")?
                "org.apache.kafka.common.security.plain.PlainLoginModule required  username=\""+username+"\"  password=\""+password+"\";":
                "org.apache.kafka.common.security.scram.ScramLoginModule required  username=\""+username+"\"  password=\""+password+"\";";



        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrap_servers);
        props.put("group.id", "kafka-ui-consumer-group");
        props.put("security.protocol", security_protocol);
        props.put("sasl.mechanism",saslMechanism);
        props.put("sasl.jaas.config",jaasString);
        props.put("key.deserializer", keyType==MessageType.JSON? "org.apache.kafka.common.serialization.StringDeserializer": KafkaAvroDeserializer.class.getName());
        props.put("value.deserializer", valueType==MessageType.JSON? "org.apache.kafka.common.serialization.StringDeserializer": KafkaAvroDeserializer.class.getName());
        props.put("schema.registry.url", schemaRegistryURL);
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

        KafkaConsumer<Object, Object> consumer = new KafkaConsumer<Object, Object>(props);

        TopicPartition topicPartition = new TopicPartition(topic, partition);
        List<TopicPartition> topicPartitions = new ArrayList<>();
        topicPartitions.add(topicPartition);
        consumer.assign(topicPartitions);
        consumer.seekToEnd(topicPartitions);
        long endPosition = consumer.position(topicPartition);

        consumer.seekToBeginning(topicPartitions);
        long beginningPosition = consumer.position(topicPartition);

        long recentMessagesStartPosition = endPosition - maxMessagesToReturn;
        if(recentMessagesStartPosition<beginningPosition)
            recentMessagesStartPosition =beginningPosition;
        consumer.seek(topicPartition, recentMessagesStartPosition);

        List<Message> messages = new ArrayList<>();
        final int giveUp = 10;
        int noRecordsCount = 0;
        while (true) {
            ConsumerRecords<Object, Object> consumerRecords =
                    consumer.poll(java.time.Duration.ofMillis(1000));

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            } else {

                consumerRecords.forEach(record -> {
                    /*System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                            record.key(), record.value(),
                            record.partition(), record.offset());*/
                    String key=null;
                    String value = null;
                    if(record.key()!=null)
                        key = record.key().toString();
                    if(record.value()!=null)
                        value = record.value().toString();
                    messages.add(new Message(key, value, record.partition(), record.offset()));
                });

                break;
            }



            //consumer.commitAsync();
        }


        consumer.close();
        Collections.reverse(messages);
        return messages;

    }
}

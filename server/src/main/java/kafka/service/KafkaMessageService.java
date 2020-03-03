package kafka.service;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import kafka.model.Message;
import kafka.model.MessageType;
import kafka.model.MessagesContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class KafkaMessageService {
    final Log logger = LogFactory.getLog(getClass());
    @Value("${security_protocol}")
    private String security_protocol;


    @Value("${bootstrap_servers}")
    private String bootstrap_servers;

    @Value("${schema.registry.url:#{null}}")
    private String schemaRegistryURL;

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

    @Value("${schema.registry.basic.auth.user.info:#{null}}")
    private String srUserInfo;


    @Value("${consumer.giveup}")
    private int giveup;

    @Value("${consumer.timeout}")
    private int timeOut;

    public MessagesContainer listMessages(String username, String password, String topic, int partition,
                                          int maxMessagesToReturn) throws KafkaException, ExecutionException, InterruptedException {
        logger.debug("Get messages called");
        Map<String, List<PartitionInfo>> topics;
        MessageType keyType;
        MessageType valueType;

        try {
            URL url = new URL(schemaRegistryURL+"/subjects/"+topic+"-key/versions/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            if(srUserInfo!=null) {
                String encoded = Base64.getEncoder().encodeToString((srUserInfo).getBytes(StandardCharsets.UTF_8));  //Java 8
                con.setRequestProperty("Authorization", "Basic "+encoded);
            }

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
            if(srUserInfo!=null) {
                String encoded = Base64.getEncoder().encodeToString((srUserInfo).getBytes(StandardCharsets.UTF_8));  //Java 8
                con.setRequestProperty("Authorization", "Basic "+encoded);
            }
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
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyType==MessageType.JSON? "org.apache.kafka.common.serialization.StringDeserializer": KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueType==MessageType.JSON? "org.apache.kafka.common.serialization.StringDeserializer": KafkaAvroDeserializer.class);
        props.put("schema.registry.url", schemaRegistryURL);

        if(srUserInfo!=null){
            props.put("basic.auth.user.info",srUserInfo);
            props.put("basic.auth.credentials.source","USER_INFO");
        }


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
        long endPosition=0;
        long recentMessagesStartPosition = 0;


            consumer.seekToEnd(topicPartitions);
            endPosition = consumer.position(topicPartition);
            logger.info("this is the end position: "+endPosition);
            consumer.seekToBeginning(topicPartitions);
            long beginningPosition = consumer.position(topicPartition);
            logger.info("this is the beginning position: "+beginningPosition);
            recentMessagesStartPosition = endPosition - maxMessagesToReturn;
            if(recentMessagesStartPosition<beginningPosition)
                recentMessagesStartPosition =beginningPosition;
            logger.info("this is the recentMessagesStartPosition position: "+recentMessagesStartPosition);
            consumer.seek(topicPartition, recentMessagesStartPosition);



        List<Message> messages = new ArrayList<>();
        //final int giveUp = 10;
        int attempts = 0;
        while (attempts<giveup ) {
            logger.info("this is the messages size:"+messages.size());
            if( messages.size()>=(endPosition-recentMessagesStartPosition))
                break;
            ConsumerRecords<Object, Object> consumerRecords =
                    consumer.poll(java.time.Duration.ofMillis(timeOut));
            attempts++;
            logger.debug("this is the attempt number:"+attempts);
            logger.debug("Got back records count:"+consumerRecords.count());
            consumerRecords.forEach(record -> {
                logger.debug("Consumer Record:(key, value, partition, offset):"+
                        record.key()+":"+ record.value()+":"+
                        record.partition()+":"+ record.offset());
                String key=null;
                String value = null;
                List<String> headers = new ArrayList<>();
                if(record.key()!=null)
                    key = record.key().toString();
                if(record.value()!=null)
                    value = record.value().toString();
                if(record.headers()!=null)
                    headers =StreamSupport.stream(record.headers().spliterator(), false).map(header -> {
                        StringBuilder sb = new StringBuilder("{\"key\":\"").append(header.key()).append("\",\"value\":\"").append(new String(header.value()) ).append("\"}");
                        return sb.toString();
                    }).collect(Collectors.toList());


                messages.add(new Message(key, value, record.partition(), record.offset(), headers, record.timestamp(), record.timestampType().toString()));
            });
            //consumer.commitAsync();
        }

        consumer.close();
        Collections.reverse(messages);
        return new MessagesContainer(false, messages);

    }
}

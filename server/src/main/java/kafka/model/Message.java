package kafka.model;

import java.util.List;

public class Message implements Comparable<Message>{
    private String key;
    private String value;
    private int partition;
    private long offset;
    long timeStamp;


    String timeStampType;



    private List<String> headers;

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeStampType() {
        return timeStampType;
    }

    public void setTimeStampType(String timeStampType) {
        this.timeStampType = timeStampType;
    }


    public Message(String key, String value, int partition, long offset, List<String> headers, long timestamp, String timestampType){
        this.key = key;
        this.value = value;
        this.partition = partition;
        this.offset = offset;
        this.headers = headers;
        this.timeStamp = timestamp;
        this.timeStampType= timestampType;
    }


    @Override
    public int compareTo(Message o) {
        return Long.compare(o.getOffset(), this.offset);
    }
}

package kafka.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class Topic {
    @JsonProperty("name")
    private String name;

    @JsonProperty("partitionCount")
    private int partitionCount;

    @JsonProperty("replicationFactor")
    private short replicationFactor;

    @JsonProperty("compacted")
    private boolean compacted;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
    }

    public Topic() {}

    public Topic(String name, int partitionCount) {
        this.name =name;
        this.partitionCount = partitionCount;
    }

    public Topic(String name, int partitionCount, short replicationFactor, boolean compacted) {
        this.name =name;
        this.partitionCount = partitionCount;
        this.replicationFactor = replicationFactor;
        this.compacted = compacted;
    }

    public short getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public boolean isCompacted() {
        return compacted;
    }

    public void setCompacted(boolean compacted) {
        this.compacted = compacted;
    }
}

package kafka.model;

public class Topic {
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

    public Topic(String name, int partitionCount) {
        this.name =name;
        this.partitionCount = partitionCount;
    }

    private String name;
    private int partitionCount;
}

export class Topic {
    name: string;
    partitionCount: number;
    replicationFactor: number;
    compacted: boolean;

    constructor(name, partitionCount, replicationFactor, compacted){
        this.name=name;
        this.partitionCount=partitionCount;
        this.replicationFactor=replicationFactor;
        this.compacted=compacted;
    }
}
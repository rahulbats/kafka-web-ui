import { Component } from '@angular/core';
import { first } from 'rxjs/operators';

import { Topic, Message } from '@app/_models';
import { TopicsService, AuthenticationService } from '@app/_services';
import { MessageService } from '@app/_services/message.service';
import { faEllipsisH, faSyncAlt, faChevronUp } from '@fortawesome/free-solid-svg-icons';

import {NgbModal, ModalDismissReasons, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

@Component({ templateUrl: 'home.component.html' })
export class HomeComponent {
    loading = false;
    messageLoading = false;
    faEllipsisH = faEllipsisH;
    faSyncAlt = faSyncAlt;
    faChevronUp = faChevronUp;
    closeResult: string;
    topics: Topic[];
    currentTopic: string;
    currentPartition: number;
    messages: Message[]=[]; 
    activeIds: string[] = [];
    topicSearch: string = ''; 
    messageSearch: string = '';
    maxMessages: number = 100;
    originalMaxMessages: number = 100;  
    page = 1;
    pageSize = 10;
    messagesSize = this.messages.length;
    

    constructor(private topicsService: TopicsService, private messageService:MessageService, private modalService: NgbModal) { }

    ngOnInit() {
        
        this.getTopics();
    }
    getTopics(){
        this.loading = true;
        this.topics = [];
        this.topicsService.getAll().pipe(first()).subscribe(topics => {
            this.loading = false;
            this.topics = topics;
        });
    }
    getMessages(topic: string, partition: number, maxMessages: number) {
        this.messages = [];
        this.currentTopic = topic;
        this.currentPartition = partition;
        window.scrollTo(0, 0);
        this.messageLoading = true;
        this.messageService.
            getMessages(topic, partition, maxMessages)
            .pipe(first()).subscribe(messages => {
            this.messageLoading = false;
            this.messages = messages;
            console.log(messages);
        });
    }
    open(content, type, value) {
        const modalRef: NgbModalRef  = this.modalService.open(content);
        modalRef.componentInstance.data=value;
        modalRef.componentInstance.type=type;
        modalRef.result.then((result) => {
            console.log(result);
          }, (reason) => {
            console.log(reason);
          });
      }
    topicSearchAction(){
       return this.topicSearch.length>0?this.topics.filter(topic=>topic.name.indexOf(this.topicSearch)>=0):this.topics;
    }
    messageSearchAction(){

        let filteredMessages = this.messages;

        if(this.messageSearch.length>0) {
            filteredMessages =this.messages.filter(message=>{
               return (message.key?message.key.indexOf(this.messageSearch)>=0:false) || (message.value?message.value.indexOf(this.messageSearch)>=0:false)    ;
            });
        }
        
        this.messagesSize = filteredMessages.length;
        return filteredMessages.slice((this.page - 1) * this.pageSize, (this.page - 1) * this.pageSize + this.pageSize);
    }
}
import { Component } from '@angular/core';
import { first } from 'rxjs/operators';

import { Topic, Message } from '@app/_models';
import { TopicsService, AuthenticationService } from '@app/_services';
import { MessageService } from '@app/_services/message.service';
import { faEllipsisH, faSyncAlt , faPlus, faMinus, faChevronDown, faChevronUp, faTrash} from '@fortawesome/free-solid-svg-icons';
import {NgbModal, ModalDismissReasons, NgbModalRef, NgbPanelChangeEvent} from '@ng-bootstrap/ng-bootstrap';
import { ModalContentComponent } from '../modal-content/modal-content.component';
import { ActivatedRoute, Router } from '@angular/router';
import { CreateTopicComponent } from '@app/create-topic/create-topic.component';


@Component({ templateUrl: 'home.component.html' })
export class HomeComponent {
    loading = false;
    messageLoading = false;
    faEllipsisH = faEllipsisH;
    faSyncAlt = faSyncAlt;
    faPlus = faPlus;
    faMinus = faMinus;
    faTrash = faTrash;
    faChevronDown = faChevronDown;
    faChevronUp = faChevronUp;
    closeResult: string;
    topics: Topic[];
    currentTopic: string='';
    currentPartition: number=0;
    isCompacted: boolean = false;
    messages: Message[]=[]; 
    topicSearch: string = ''; 
    messageSearch: string = '';
    maxMessages: number = 100;
    originalMaxMessages: number = 100;  
    page = 1;
    pageSize = 10;
    messagesSize = this.messages.length;
    topicError=null;
    messageError=null;
    hideOldKeyMessages=false;
    JSON=JSON;
    constructor(private topicsService: TopicsService, private messageService:MessageService, private modalService: NgbModal, private route: ActivatedRoute,private router: Router) { this.JSON = JSON; }


    ngOnInit() {
        this.route.queryParams.subscribe(params=>{
            this.currentTopic = params['topic'];
            this.currentPartition = +params['partition'];
            if(this.currentTopic && this.currentTopic!='') {
                this.getMessages( );
            }
        })
        this.getTopics();
    }
    
    
    getTopics(){
        this.topicError=null;
        this.loading = true;
        this.topics = [];
        this.topicsService.getAll()
            .pipe(first())
            .subscribe(
                topics => {
                    this.loading = false;
                    this.topics = topics;
                },
                error=> {
                    this.loading = false;
                    this.topicError= error;
                }
                );
    }
    gotoMessages(topic: string, partition: number){
        this.router.navigate(['/home'],{ 'queryParams': {'topic': topic, 'partition': partition}}); 
    }
    getMessages( ) {
        this.messages = [];
        this.messageError = null;
        //this.currentTopic = topic;
        //this.currentPartition = partition;
        window.scrollTo(0, 0);
        this.messageLoading = true;
        this.messageService.
            getMessages(this.currentTopic, this.currentPartition, this.maxMessages, this.hideOldKeyMessages)
            .pipe(first())
            .subscribe(
                messagesContainer => {
                    this.messageLoading = false;
                    this.messages = messagesContainer.messages;
                    this.isCompacted = messagesContainer.compacted;
                },
                error=> {
                    this.messageLoading = false;
                    this.messageError= error;
                }
                );
    }
    open(content, type, value) {
        const modalRef: NgbModalRef  = this.modalService.open(ModalContentComponent);
        modalRef.componentInstance.data=value;
        modalRef.componentInstance.type=type;
        modalRef.result.then((result) => {
            console.log(result);
          }, (reason) => {
            console.log(reason);
          });
      }
    deleteTopic(topicName:string) {
        this.topicsService.deleteTopic(topicName)
        .pipe(first())
        .subscribe(
            result => {
                this.getTopics();
            },
            error=> {
                this.topicError=error;
            }
            );
    }  
    openCreateTopic() {
        const modalRef: NgbModalRef  = this.modalService.open(CreateTopicComponent);
        //modalRef.componentInstance.data=value;
        //modalRef.componentInstance.type=type;
        modalRef.result.then((result) => {
            console.log(result);
            
          }, (reason) => {
            console.log(reason);
            this.getTopics();
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
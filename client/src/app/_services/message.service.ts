import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { MessagesContainer } from '@app/_models/messagesContainer';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private http: HttpClient) { }

  getMessages(topic: string, partition: number, maxMessages: number) {
    return this.http.get<MessagesContainer>
                  (`${environment.apiUrl}/api/messages/${topic}/${partition}/${maxMessages}`);
  }

}

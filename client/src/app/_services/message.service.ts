import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { Message } from '@app/_models';
@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private http: HttpClient) { }

  getMessages(topic: string, partition: number, maxMessages: number) {
    return this.http.get<Message[]>
                  (`${environment.apiUrl}/api/messages/${topic}/${partition}/${maxMessages}`);
  }

}

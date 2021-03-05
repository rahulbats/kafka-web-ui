import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import {Topic} from '@app/_models';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TopicsService {

  constructor(private http: HttpClient) { }

  getAll() {
    return this.http.get<Topic[]>(`${environment.apiUrl}/api/topics`);
  }

  createTopic(topic: Topic) {
    return this.http.post<any>(`${environment.apiUrl}/api/topics`,topic);
  }

  deleteTopic(topic: string) {
    return this.http.delete<any>(`${environment.apiUrl}/api/topics/${topic}`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import {Topic} from '@app/_models';

@Injectable({
  providedIn: 'root'
})
export class TopicsService {

  constructor(private http: HttpClient) { }

  getAll() {
    return this.http.get<Topic[]>(`${environment.apiUrl}/api/topics`);
}
}

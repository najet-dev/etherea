import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { SigninRequest } from '../components/models/signinRequest.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getUserId(id: number): Observable<SigninRequest> {
    return this.httpClient.get<SigninRequest>(`${this.apiUrl}/users/${id}`);
  }
}

import { Injectable } from '@angular/core';
import { SignupRequest } from '../components/models/signupRequest.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getAllUsers(): Observable<SignupRequest[]> {
    return this.httpClient.get<SignupRequest[]>(
      `${this.apiUrl}/api/admin/users`
    );
  }
}

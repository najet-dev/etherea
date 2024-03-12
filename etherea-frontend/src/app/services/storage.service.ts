import { Injectable } from '@angular/core';
import { User } from '../components/models/user.model';

const USER_KEY = 'authenticated-user'; // Ajoutez la déclaration de USER_KEY ici

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  constructor() {}

  saveUser(user: User) {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  getSavedUser(): User | null {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return null;
  }

  clean(): void {
    window.localStorage.clear();
  }
  // register(data: SignupRequest): Observable<SignupRequest> {
  //   return this.httpClient.post<SignupRequest>(
  //     `${this.apiUrl}/api/auth/signup`,
  //     data
  //   );
  // }

  // login(data: SigninRequest): Observable<SigninRequest> {
  //   return this.httpClient.post<SigninRequest>(
  //     `${this.apiUrl}/api/auth/signin`,
  //     data
  //   );
  // }
}

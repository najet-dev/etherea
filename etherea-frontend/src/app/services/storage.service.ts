import { Injectable } from '@angular/core';
import { SigninRequest } from '../components/models/signinRequest.moodel';

const USER_KEY = 'authenticated-user';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  constructor() {}

  saveUser(signin: SigninRequest) {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(signin));
  }

  getSavedUser(): SigninRequest | null {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return null;
  }

  clean(): void {
    window.localStorage.clear();
  }
}

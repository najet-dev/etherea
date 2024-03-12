import { Component } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { SignupRequest } from '../models/user.model';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {
  signUpRequest: SignupRequest = {
    firstName: '',
    lastName: '',
    username: '',
    password: '',
    // Ajoutez d'autres propriétés au besoin
  };

  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService) {}

  signUp(): void {
    this.authService.register(this.signUpRequest).subscribe(
      (data: any) => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      (error: any) => {
        console.error(error);
        this.errorMessage =
          error.error?.message || 'An unexpected error occurred.';
        this.isSignUpFailed = true;
      }
    );
  }
}

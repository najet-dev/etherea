import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { User } from '../models/user.model';
import { SigninRequest } from '../models/signinRequest.moodel';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css'],
})
export class SigninComponent implements OnInit, OnDestroy {
  errorMessage: string = '';
  signinRequest: SigninRequest = { username: '', password: '' };
  AuthUserSub?: Subscription;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.AuthUserSub = this.authService.AuthenticatedUser$.subscribe({
      next: (user) => {
        if (user) {
          this.router.navigate(['home']);
        }
      },
    });
  }

  onSubmitLogin(formLogin: NgForm) {
    if (!formLogin.valid) {
      return;
    }

    this.authService
      .login(this.signinRequest.username, this.signinRequest.password)
      .subscribe({
        next: (userData) => {
          this.router.navigate(['home']);
        },
        error: (err) => {
          this.errorMessage = err;
          console.log(err);
        },
      });
  }

  ngOnDestroy() {
    if (this.AuthUserSub) {
      this.AuthUserSub.unsubscribe();
    }
  }

  protected readonly console = console;
}

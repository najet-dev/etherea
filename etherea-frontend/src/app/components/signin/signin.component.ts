import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { SignupRequest } from '../models/SignupRequest.model';
import { StorageService } from 'src/app/services/storage.service';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css'],
})
export class SigninComponent implements OnInit, OnDestroy {
  errorMessage: string = '';
  loginForm!: FormGroup;
  submitted = false;
  AuthUserSub?: Subscription;

  public errorMessages = {
    username: [
      { type: 'required', message: 'Email requis' },
      { type: 'email', message: 'Email doit être valide' },
    ],
    password: [
      {
        type: 'minlength',
        message: 'Le mot de passe doit contenir au minimum 8 caractères',
      },
      { type: 'required', message: 'Le mot de passe est requis' },
      {
        type: 'pattern',
        message:
          'Le mot de passe doit contenir une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial au minimum',
      },
    ],
  };

  constructor(
    private authService: AuthService,
    private storageService: StorageService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {}

  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }

  ngOnInit() {
    this.AuthUserSub = this.authService.AuthenticatedUser$.subscribe({
      next: (user) => {
        if (user) {
          this.router.navigate(['/']);
        }
      },
    });

    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.email]],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern(
            /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^_/:&*-]).{8,}$/
          ),
        ],
      ],
    });
  }

  onSubmit() {
    console.log('SigninComponent: Submitting signin form');
    console.log('Username:', this.loginForm.value.username);
    console.log('Password:', this.loginForm.value.password);
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (userData) => {
        // Redirection vers la page d'accueil après une connexion réussie
        this.router.navigate(['/']); // Chemin vers la page d'accueil
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

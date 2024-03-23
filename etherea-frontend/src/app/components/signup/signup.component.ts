import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { StorageService } from 'src/app/services/storage.service';
import { SigninRequest } from '../models/signinRequest.model';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent implements OnInit, OnDestroy {
  errorMessage: string = '';
  signupForm!: FormGroup;
  submitted = false;
  AuthUserSub?: Subscription;

  public errorMessages = {
    firstName: [{ type: 'required', message: 'Prénom requis' }],
    lastName: [{ type: 'required', message: 'Nom requis' }],
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

  get firstName() {
    return this.signupForm.get('firstName');
  }

  get lastName() {
    return this.signupForm.get('lastName');
  }

  get username() {
    return this.signupForm.get('username');
  }

  get password() {
    return this.signupForm.get('password');
  }

  ngOnInit() {
    this.AuthUserSub = this.authService.AuthenticatedUser$.subscribe({
      next: (user) => {
        if (user) {
          this.router.navigate(['/']);
        }
      },
    });

    this.signupForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
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
    this.submitted = true;

    if (this.signupForm.invalid) {
      return;
    }

    this.authService.signup(this.signupForm.value).subscribe({
      next: (userData) => {
        this.router.navigate(['/signin']);
        this.signupForm.reset(); // Réinitialiser le formulaire après une soumission réussie
      },
    });
  }

  ngOnDestroy() {
    if (this.AuthUserSub) {
      this.AuthUserSub.unsubscribe();
    }
  }
}

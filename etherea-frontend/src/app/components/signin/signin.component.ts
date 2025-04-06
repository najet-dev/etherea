import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { tap } from 'rxjs/operators';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Role } from '../models/role.enum';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css'],
})
export class SigninComponent implements OnInit {
  errorMessage: string = '';
  loginForm!: FormGroup;
  submitted = false;
  private destroyRef = inject(DestroyRef);

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
    this.authService.AuthenticatedUser$.pipe(
      tap((user) => {
        if (user) {
          this.router.navigate(['/']);
        }
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();

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
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    this.authService
      .signin(this.loginForm.value)
      .pipe(
        tap({
          next: (user) => {
            // Si l'utilisateur est un admin, rediriger vers l'admin dashboard
            if (this.authService.isAdmin()) {
              this.router.navigate(['/admin/admin-dashboard']);
            } else {
              // Sinon, rediriger vers la page d'accueil
              this.router.navigate(['/']);
            }
            this.loginForm.reset();
          },
          error: (err) => {
            if (err.status === 401) {
              this.errorMessage = "L'email ou le mot de passe est invalide.";
            } else {
              this.errorMessage =
                'Une erreur est survenue. Veuillez réessayer plus tard.';
            }
          },
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }
}

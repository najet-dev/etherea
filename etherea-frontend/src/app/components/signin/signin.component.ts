import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { tap } from 'rxjs/operators';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css'],
})
export class SigninComponent {
  errorMessage: string = '';
  loginForm!: FormGroup;
  submitted = false;
  private destroyRef = inject(DestroyRef);

  // Custom error messages used in the form template
  public errorMessages = {
    username: [
      {
        type: 'email',
        message:
          'Veuillez saisir une adresse e-mail valide sans espace (ex. : prenom.nom@domaine.com).',
      },
    ],
    password: [
      {
        type: 'minlength',
        message:
          'Veuillez saisir au moins 8 caractères, dont une majuscule, une minuscule, un chiffre et un caractère spécial.',
      },
    ],
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {}

  // Getters to access form fields easily in the template
  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }

  ngOnInit() {
    // Redirect if user is already authenticated
    this.authService.AuthenticatedUser$.pipe(
      tap((user) => {
        if (user) {
          this.router.navigate(['/']);
        }
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();

    // Initialize login form with validation rules
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

    // Attempt login with form data
    this.authService
      .signin(this.loginForm.value)
      .pipe(
        tap({
          next: (user) => {
            // Redirect based on user role
            if (this.authService.isAdmin()) {
              this.router.navigate(['/admin/admin-dashboard']);
            } else {
              this.router.navigate(['/']);
            }
            this.loginForm.reset();
          },
          error: (err) => {
            // Display error based on server response
            if (err.status === 401) {
              this.errorMessage = err.message;
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

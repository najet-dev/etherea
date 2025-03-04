import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { tap } from 'rxjs/operators';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent implements OnInit {
  errorMessage: string = '';
  signupForm!: FormGroup;
  submitted = false; // Indicateur pour savoir si le formulaire a été soumis
  private destroyRef = inject(DestroyRef);

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
    private router: Router,
    private formBuilder: FormBuilder
  ) {}

  get lastName() {
    return this.signupForm.get('lastName');
  }

  get firstName() {
    return this.signupForm.get('firstName');
  }

  get username() {
    return this.signupForm.get('username');
  }

  get password() {
    return this.signupForm.get('password');
  }

  ngOnInit() {
    this.authService.AuthenticatedUser$.pipe(
      tap((user) => {
        if (user) {
          // Si un utilisateur est authentifié
          this.router.navigate(['/']);
        }
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();

    this.signupForm = this.formBuilder.group({
      lastName: ['', [Validators.required]],
      firstName: ['', [Validators.required]],
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
    // Indique que le formulaire a été soumis
    this.submitted = true;

    if (this.signupForm.invalid) {
      // Vérifie si le formulaire est invalide
      return; // Arrête le traitement si le formulaire est invalide
    }

    this.authService
      .signup(this.signupForm.value)
      .pipe(
        tap({
          next: () => {
            this.router.navigate(['/signin']);
            this.signupForm.reset();
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

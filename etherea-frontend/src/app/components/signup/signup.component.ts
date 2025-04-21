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
  submitted = false;
  private destroyRef = inject(DestroyRef);

  // Custom error messages for each form field
  public errorMessages = {
    firstName: [{ type: 'required', message: 'Le prénom est obligatoire.' }],
    lastName: [{ type: 'required', message: 'Le nom est obligatoire.' }],
    username: [
      {
        type: 'email',
        message: 'Veuillez saisir une adresse e-mail valide.',
      },
      {
        type: 'emailExists',
        message: 'Cet email est déjà utilisé.',
      },
    ],
    password: [
      {
        type: 'minlength',
        message: 'Le mot de passe doit contenir au moins 8 caractères.',
      },
    ],
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {}

  // Getters for easier access to form fields in the template
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
    // Redirect if user is already authenticated
    this.authService.AuthenticatedUser$.pipe(
      tap((user) => {
        if (user) {
          this.router.navigate(['/']);
        }
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();

    // Form initialization with validation rules
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
    this.submitted = true;

    if (this.signupForm.invalid) {
      return;
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
            // Error handling based on HTTP status
            if (err.status === 401) {
              this.errorMessage = "L'email ou le mot de passe est invalide.";
            } else if (err.status === 409) {
              this.signupForm.get('username')?.setErrors({ emailExists: true });
              this.errorMessage = '';
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

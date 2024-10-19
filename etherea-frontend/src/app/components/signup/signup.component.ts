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
export class SignupComponent {
  errorMessage: string = ''; // Message d'erreur pour afficher les erreurs
  signupForm!: FormGroup; // Formulaire d'inscription
  submitted = false; // Indicateur pour savoir si le formulaire a été soumis
  private destroyRef = inject(DestroyRef); // Inject DestroyRef

  public errorMessages = {
    // Messages d'erreur pour les champs du formulaire
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

  get firstName() {
    // Méthode pour récupérer le champ du prénom du formulaire
    return this.signupForm.get('firstName');
  }

  get lastName() {
    // Méthode pour récupérer le champ du nom du formulaire
    return this.signupForm.get('lastName');
  }

  get username() {
    // Méthode pour récupérer le champ de l'email du formulaire
    return this.signupForm.get('username');
  }

  get password() {
    // Méthode pour récupérer le champ du mot de passe du formulaire
    return this.signupForm.get('password');
  }

  ngOnInit() {
    // Méthode du cycle de vie d'Angular appelée après l'initialisation du composant
    this.authService.AuthenticatedUser$.pipe(
      tap((user) => {
        // Traitement lorsque de nouvelles données sont émises par l'observable
        if (user) {
          // Si un utilisateur est authentifié
          this.router.navigate(['/']);
        }
      }),
      takeUntilDestroyed(this.destroyRef) // Use takeUntilDestroyed
    ).subscribe();

    this.signupForm = this.formBuilder.group({
      // Initialisation du formulaire d'inscription avec les champs et les validateurs requis
      firstName: ['', [Validators.required]], // Champ du prénom avec validation de la présence
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
    // Méthode appelée lorsque le formulaire est soumis
    this.submitted = true; // Indique que le formulaire a été soumis

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
              // Le statut 401 indique une authentification invalide
              this.errorMessage = "L'email ou le mot de passe est invalide.";
            } else {
              // Pour toutes les autres erreurs, afficher un message générique
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

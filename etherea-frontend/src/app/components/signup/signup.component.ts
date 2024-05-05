import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent implements OnInit, OnDestroy {
  errorMessage: string = ''; // Message d'erreur pour afficher les erreurs
  signupForm!: FormGroup; // Formulaire d'inscription
  submitted = false; // Indicateur pour savoir si le formulaire a été soumis
  AuthUserSub?: Subscription; // Abonnement à l'observable de l'utilisateur authentifié

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
    this.AuthUserSub = this.authService.AuthenticatedUser$.subscribe({
      // Abonnement à l'observable de l'utilisateur authentifié
      next: (user) => {
        // Traitement lorsque de nouvelles données sont émises par l'observable
        if (user) {
          // Si un utilisateur est authentifié
          this.router.navigate(['/']);
        }
      },
    });

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

    this.authService.signup(this.signupForm.value).subscribe({
      // Appel du service d'authentification pour l'inscription de l'utilisateur
      next: (userData) => {
        // Traitement lorsque l'appel réussit
        this.router.navigate(['/signin']);
        this.signupForm.reset();
      },
    });
  }

  ngOnDestroy() {
    // Méthode du cycle de vie d'Angular appelée juste avant la destruction du composant
    if (this.AuthUserSub) {
      // Vérifie si l'abonnement existe
      this.AuthUserSub.unsubscribe();
    }
  }
}

import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';
import { tap } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-email',
  templateUrl: './email.component.html',
  styleUrls: ['./email.component.css'],
})
export class EmailComponent implements OnInit {
  userId: number = 0;
  updateEmailForm!: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  submitted = false;
  private destroyRef = inject(DestroyRef);

  public errorMessages = {
    currentEmail: [
      { type: 'required', message: 'Email actuel requis' },
      { type: 'email', message: 'Email doit être valide' },
    ],
    newEmail: [
      { type: 'required', message: 'Nouveau email requis' },
      { type: 'email', message: 'Email doit être valide' },
    ],
  };

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {}

  get currentEmail() {
    return this.updateEmailForm.get('currentEmail');
  }

  get newEmail() {
    return this.updateEmailForm.get('newEmail');
  }

  ngOnInit() {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        console.log('Objet utilisateur reçu:', user);
        if (user && user.id) {
          this.userId = user.id;
        } else {
          console.error("L'ID utilisateur n'est pas défini.");
        }
      });

    this.updateEmailForm = this.formBuilder.group({
      currentEmail: ['', [Validators.required, Validators.email]],
      newEmail: ['', [Validators.required, Validators.email]],
    });
  }

  onSubmit() {
    this.submitted = true;

    if (this.updateEmailForm.invalid) {
      return;
    }

    if (!this.userId) {
      console.error("Erreur : L'ID utilisateur est manquant !");
      this.errorMessage = 'Une erreur est survenue, veuillez vous reconnecter.';
      return;
    }

    const updateEmailRequest = {
      userId: this.userId, // Ajoute l'ID utilisateur
      currentEmail: this.updateEmailForm.value.currentEmail,
      newEmail: this.updateEmailForm.value.newEmail,
    };

    this.userService
      .updateEmail(updateEmailRequest)
      .pipe(
        tap({
          next: () => {
            this.successMessage = 'Votre email a été mis à jour avec succès.';
            this.errorMessage = '';
            this.updateEmailForm.reset();

            // Disparition automatique du message de succès après 5 secondes
            setTimeout(() => {
              this.successMessage = '';
            }, 5000);
          },
          error: (err) => {
            console.error('Erreur mise à jour email :', err);
            if (err.status === 400) {
              this.errorMessage = "L'email actuel est incorrect.";
            } else {
              this.errorMessage =
                'Une erreur est survenue, veuillez réessayer.';
            }
            this.successMessage = '';
          },
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }
}

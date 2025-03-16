import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { tap } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-update-email',
  templateUrl: './update-email.component.html',
  styleUrls: ['./update-email.component.css'],
})
export class UpdateEmailComponent {
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
      userId: this.userId,
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

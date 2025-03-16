import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { tap } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';
import { UpdatePasswordRequest } from '../models/updatePasswordRequest.model';

@Component({
  selector: 'app-update-password',
  templateUrl: './update-password.component.html',
  styleUrls: ['./update-password.component.css'],
})
export class UpdatePasswordComponent {
  userId: number = 0;
  updatePasswordForm!: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  submitted = false;
  private destroyRef = inject(DestroyRef);

  public errorMessages = {
    currentPassword: [
      { type: 'required', message: 'Mot de passe actuel requis' },
      {
        type: 'minlength',
        message: 'Mot de passe trop court (min 6 caractères)',
      },
    ],
    newPassword: [
      { type: 'required', message: 'Nouveau mot de passe requis' },
      {
        type: 'minlength',
        message: 'Mot de passe trop court (min 6 caractères)',
      },
    ],
    confirmPassword: [
      { type: 'required', message: 'Confirmation du mot de passe requise' },
      { type: 'mustMatch', message: 'Les mots de passe ne correspondent pas' },
    ],
  };

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) {}

  get currentPassword() {
    return this.updatePasswordForm.get('currentPassword');
  }

  get newPassword() {
    return this.updatePasswordForm.get('newPassword');
  }

  get confirmPassword() {
    return this.updatePasswordForm.get('confirmPassword');
  }

  ngOnInit() {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        if (user && user.id) {
          this.userId = user.id;
        } else {
          console.error("L'ID utilisateur est manquant.");
        }
      });

    this.updatePasswordForm = this.formBuilder.group(
      {
        currentPassword: ['', [Validators.required, Validators.minLength(6)]],
        newPassword: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]],
      },
      { validator: this.mustMatch('newPassword', 'confirmPassword') }
    );
  }

  mustMatch(passwordKey: string, confirmPasswordKey: string) {
    return (formGroup: FormGroup) => {
      const passwordControl = formGroup.controls[passwordKey];
      const confirmPasswordControl = formGroup.controls[confirmPasswordKey];

      if (
        confirmPasswordControl.errors &&
        !confirmPasswordControl.errors['mustMatch']
      ) {
        return;
      }

      if (passwordControl.value !== confirmPasswordControl.value) {
        confirmPasswordControl.setErrors({ mustMatch: true });
      } else {
        confirmPasswordControl.setErrors(null);
      }
    };
  }

  onSubmit() {
    this.submitted = true;

    if (this.updatePasswordForm.invalid) {
      return;
    }

    if (!this.userId) {
      this.errorMessage = 'Une erreur est survenue, veuillez vous reconnecter.';
      return;
    }

    const updatePasswordRequest: UpdatePasswordRequest = {
      currentPassword: this.updatePasswordForm.value.currentPassword,
      newPassword: this.updatePasswordForm.value.newPassword,
      confirmPassword: this.updatePasswordForm.value.confirmPassword,
      userId: this.userId,
    };

    this.userService
      .updatePassword(updatePasswordRequest)
      .pipe(
        tap({
          next: () => {
            this.successMessage =
              'Votre mot de passe a été mis à jour avec succès.';
            this.errorMessage = '';
            this.updatePasswordForm.reset();

            setTimeout(() => {
              this.successMessage = '';
            }, 5000);
          },
          error: (err) => {
            console.error('Erreur mise à jour mot de passe :', err);
            this.errorMessage =
              err.message || 'Une erreur est survenue, veuillez réessayer.';
            this.successMessage = '';
          },
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }
}

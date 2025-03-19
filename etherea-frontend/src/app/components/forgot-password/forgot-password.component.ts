import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppFacade } from 'src/app/services/appFacade.service';
import { PasswordResetService } from 'src/app/services/password-reset.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  forgotPasswordForm: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(private fb: FormBuilder, private appFacade: AppFacade) {
    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onSubmit() {
    if (this.forgotPasswordForm.valid) {
      this.isLoading = true;
      const email = this.forgotPasswordForm.value.email;

      this.appFacade.sendResetLink({ email }).subscribe({
        next: () => {
          this.successMessage = `Si un compte est retrouvé à l'adresse email "${email}", vous allez recevoir un email pour réinitialiser votre mot de passe.`;
          this.errorMessage = '';
          this.forgotPasswordForm.reset();
        },
        error: () => {
          this.errorMessage =
            "Erreur lors de l'envoi de l'email. Veuillez réessayer.";
          this.successMessage = '';
        },
        complete: () => {
          this.isLoading = false;
        },
      });
    }
  }
}

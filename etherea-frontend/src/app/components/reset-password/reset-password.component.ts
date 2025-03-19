import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PasswordResetService } from '../../services/password-reset.service';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent implements OnInit {
  resetForm!: FormGroup;
  token!: string;
  message: string = '';

  errorMessages = {
    newPassword: [
      { type: 'required', message: 'Ce champ est obligatoire' },
      {
        type: 'minlength',
        message: 'Le mot de passe doit contenir au moins 8 caractères.',
      },
    ],
    confirmPassword: [
      {
        type: 'required',
        message: 'Ce champ est obligatoire',
      },
    ],
  };

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private passwordResetService: PasswordResetService,
    private appFacade: AppFacade
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'];
    if (!this.token) {
      this.message = 'Lien de réinitialisation invalide.';
      setTimeout(() => this.router.navigate(['/signin']), 3000);
      return;
    }

    this.resetForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
    });
  }

  passwordsMatch(): boolean {
    return (
      this.resetForm.value.newPassword === this.resetForm.value.confirmPassword
    );
  }

  onSubmit(): void {
    if (!this.passwordsMatch()) {
      this.message = 'Les mots de passe ne correspondent pas.';
      return;
    }

    const requestData = {
      token: this.token,
      newPassword: this.resetForm.value.newPassword,
      confirmPassword: this.resetForm.value.confirmPassword,
    };

    this.appFacade.resetPassword(requestData).subscribe({
      next: (response) => {
        this.message = response.message;
        setTimeout(() => this.router.navigate(['/signin']), 4000);
      },
      error: (error) => {
        this.message =
          error.error.error || 'Erreur lors de la réinitialisation.';
      },
    });
  }
}

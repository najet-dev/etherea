import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PasswordResetService } from '../../services/password-reset.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent implements OnInit {
  resetForm!: FormGroup;
  token!: string;
  message: string = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private passwordResetService: PasswordResetService
  ) {}

  ngOnInit(): void {
    // Récupérer le token depuis l'URL
    this.token = this.route.snapshot.queryParams['token'];
    // Vérifier si le token est bien présent, sinon rediriger
    if (!this.token) {
      this.message = 'Lien de réinitialisation invalide.';
      setTimeout(() => this.router.navigate(['/login']), 3000);
      return;
    }

    this.resetForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
    });
  }

  // Vérification de la correspondance des mots de passe
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

    this.passwordResetService.resetPassword(requestData).subscribe({
      next: (response) => {
        this.message = response.message;
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (error) => {
        this.message =
          error.error.error || 'Erreur lors de la réinitialisation.';
      },
    });
  }
}

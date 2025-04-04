import { Component } from '@angular/core';
import { SignupRequest } from '../../models/signupRequest.model';
import { AuthService } from 'src/app/services/auth.service';
import { Role } from '../../models/role.enum';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css'],
})
export class AddUserComponent {
  user: SignupRequest = {
    id: 0,
    firstName: '',
    lastName: '',
    username: '',
    password: '',
    roles: [Role.ROLE_USER],
  };
  successMessage = '';
  availableRoles: string[] = Object.values(Role);

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    if (
      this.user.firstName.trim() &&
      this.user.lastName.trim() &&
      this.user.username.trim() &&
      this.user.password.trim() &&
      this.user.roles.length > 0
    ) {
      this.authService.signup(this.user).subscribe({
        next: (response) => {
          console.log('Utilisateur ajouté avec succès:', response);
          this.successMessage = 'Utilisateur ajouté avec succès.';
          this.resetForm();
        },
        error: (error) => {
          console.error("Erreur lors de l'ajout de l'utilisateur:", error);
        },
        complete: () => {
          console.log('Ajout terminé.');
        },
      });
    } else {
      console.log(
        'Erreur: Veuillez remplir toutes les informations nécessaires.'
      );
    }
  }

  resetForm(): void {
    this.user = {
      id: 0,
      firstName: '',
      lastName: '',
      username: '',
      password: '',
      roles: [Role.ROLE_USER],
    };
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }
}

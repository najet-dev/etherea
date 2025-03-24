import { Component } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { SignupRequest } from '../../models/signupRequest.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
})
export class UserListComponent {
  users: SignupRequest[] = [];

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        console.log(users); // Vérification des données reçues
        this.users = users;
      },
      error: (error) => {
        console.error(
          'Erreur lors de la récupération des utilisateurs:',
          error
        );
      },
      complete: () => {
        console.log('Récupération des utilisateurs terminée.');
      },
    });
  }

  deleteUser(userId: number): void {
    this.userService.deleteUser(userId).subscribe(() => {
      this.getUsers(); // Rafraîchir la liste après la suppression
    });
  }
}

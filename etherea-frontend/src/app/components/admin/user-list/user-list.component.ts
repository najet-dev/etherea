import { Component, DestroyRef, inject } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { SignupRequest } from '../../models/signupRequest.model';
import { catchError, of, switchMap, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
})
export class UserListComponent {
  users: SignupRequest[] = [];
  private destroyRef = inject(DestroyRef);

  constructor(private userService: UserService, private appFacade: AppFacade) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService
      .getAllUsers()
      .pipe(
        tap((users) => {
          if (Array.isArray(users)) {
            this.users = users;
          } else {
            console.error('Données invalides reçues :', users);
            this.users = [];
          }
        }),
        catchError((error) => {
          console.error(
            'Erreur lors de la récupération des utilisateurs:',
            error
          );
          this.users = [];
          return of([]);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  deleteUser(userId: number) {
    this.userService
      .deleteUser(userId)
      .pipe(
        switchMap(() => this.appFacade.getUsers()), // Recharger la liste des produits après suppression
        catchError((error) => {
          console.error('Erreur lors de la suppression du produit:', error);
          return of([]);
        })
      )
      .subscribe((users) => {
        this.users = users;
      });
  }
}

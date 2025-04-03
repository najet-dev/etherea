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
  totalPages: number = 0;
  totalElements: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  private destroyRef = inject(DestroyRef);

  constructor(private appFacade: AppFacade) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(page: number = 0): void {
    this.appFacade
      .getAllUsers(page, this.pageSize)
      .pipe(
        tap((response) => {
          this.users = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.currentPage = page;
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

  deleteUser(userId: number): void {
    this.appFacade
      .deleteUser(userId)
      .pipe(
        switchMap(() =>
          this.appFacade.getAllUsers(this.currentPage, this.pageSize)
        ),
        catchError((error) => {
          console.error(
            'Erreur lors de la suppression de l’utilisateur:',
            error
          );
          return of({ content: [], totalElements: 0, totalPages: 0 });
        })
      )
      .subscribe((response) => {
        this.users = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
      });
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.loadUsers(this.currentPage - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.loadUsers(this.currentPage + 1);
    }
  }
}

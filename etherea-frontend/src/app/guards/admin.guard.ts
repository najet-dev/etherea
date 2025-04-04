import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { Role } from '../components/models/role.enum';

export const AdminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.getCurrentUser().pipe(
    map((user) => {
      if (user?.roles.includes(Role.ROLE_ADMIN)) {
        return true;
      } else {
        router.navigate(['/signin']);
        return false;
      }
    }),
    catchError(() => {
      router.navigate(['/signin']);
      return of(false);
    })
  );
};

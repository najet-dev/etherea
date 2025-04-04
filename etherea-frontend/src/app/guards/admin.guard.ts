import { inject } from '@angular/core';
import { CanActivateChildFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../components/models/role.enum';
import { map, catchError, of } from 'rxjs';
export const AdminGuard: CanActivateChildFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.getCurrentUser().pipe(
    map((user) => {
      console.log('AdminGuard - user reçu:', user);

      if (user && user.roles.includes(Role.ROLE_ADMIN)) {
        return true;
      } else {
        console.log('Accès refusé, redirection vers /signin');
        router.navigate(['/signin']);
        return false;
      }
    }),
    catchError(() => {
      console.log('Erreur dans AdminGuard, redirection vers /signin');
      router.navigate(['/signin']);
      return of(false);
    })
  );
};

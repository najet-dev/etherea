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
      console.log('AdminGuard vérifié');

      console.log('Current user:', user); // Log pour vérifier l'utilisateur
      if (user && authService.isAdmin()) {
        return true; // Accès autorisé pour l'admin
      } else {
        router.navigate(['/signin']);
        console.log('Accès refusé');

        return false; // Accès refusé, redirection vers la page de connexion
      }
    }),
    catchError((error) => {
      console.error('Erreur lors de la vérification des rôles:', error);
      router.navigate(['/signin']);
      return of(false); // En cas d'erreur, refuser l'accès
    })
  );
};

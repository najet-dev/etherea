import {
  CanActivateFn,
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Injectable, inject } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): Observable<boolean | UrlTree> => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return authService.AuthenticatedUser$.pipe(
    take(1),
    map((signin) => {
      console.log('Auth Guard: Checking authentication');

      const roles = route.data['roles'] as string[]; // Assuming roles are string[]

      if (
        signin &&
        signin.roles &&
        roles &&
        signin.roles.some((userRole) => roles.includes(userRole.name))
      ) {
        return true;
      }

      console.error(
        'Auth Guard: User not authenticated, redirecting to signin page'
      );

      return router.createUrlTree(['/signin']);
    })
  );
};

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
    map((user) => {
      const { roles } = route.data;

      if (user && user.roles && roles && roles.includes(user.roles[0].name)) {
        return true;
      }

      if (user) {
        return router.createUrlTree(['/forbidden']);
      }

      return router.createUrlTree(['/login']);
    })
  );
};

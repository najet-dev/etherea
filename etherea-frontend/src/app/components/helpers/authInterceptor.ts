import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { AuthService } from 'src/app/services/auth.service';
import { Observable, catchError, switchMap, take, throwError } from 'rxjs';
import { Router } from '@angular/router';
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private router: Router) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return this.authService.AuthenticatedUser$.pipe(
      take(1),
      switchMap((signin) => {
        if (!signin) {
          return next.handle(request);
        }

        // Cloner la requête et ajouter le jeton d'authentification dans l'en-tête Authorization
        const modifiedRequest = request.clone({
          setHeaders: {
            Authorization: `Bearer ${signin.accessToken}`,
          },
        });

        return next.handle(modifiedRequest).pipe(
          catchError((err) => {
            if (err instanceof HttpErrorResponse) {
              console.error('HTTP Interceptor: HTTP error occurred', err);

              switch (err.status) {
                case 403:
                  console.error('HTTP Interceptor: 403 Forbidden Error');

                  this.router.navigate(['/forbidden']);

                  break;
              }
            }
            return throwError(() => err);
          })
        );
      })
    );
  }
}

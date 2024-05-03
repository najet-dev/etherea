import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { AuthService } from 'src/app/services/auth.service';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap, take } from 'rxjs/operators';
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

        const modifiedRequest = request.clone({
          setHeaders: {
            Authorization: `Bearer ${signin.accessToken}`,
          },
        });

        return next.handle(modifiedRequest).pipe(
          catchError((err) => {
            if (err instanceof HttpErrorResponse) {
              console.error('HTTP Interceptor: HTTP error occurred', err); // Ajout du log ici

              switch (err.status) {
                case 401:
                  console.error('HTTP Interceptor: 401 Unauthorized Error');
                  // Rediriger vers la page de connexion, par exemple
                  break;
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

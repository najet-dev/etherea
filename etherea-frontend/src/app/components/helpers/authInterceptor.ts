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

        const modifiedRequest = request.clone({
          setHeaders: {
            Authorization: `Bearer ${signin.accessToken}`,
          },
        });

        return next.handle(modifiedRequest);
      }),
      catchError((err) => this.handleError(err))
    );
  }

  private handleError(err: HttpErrorResponse): Observable<never> {
    if (err instanceof HttpErrorResponse) {
      console.error('HTTP Interceptor: HTTP error occurred', err);

      switch (err.status) {
        case 403:
          console.error('HTTP Interceptor: 403 Forbidden Error');
          this.router.navigate(['/forbidden']);
          break;

        // Gestion d'autres erreurs HTTP si nécessaire

        default:
          console.error(`HTTP Interceptor: Error ${err.status}`);
      }
    }
    return throwError(() => err);
  }
}

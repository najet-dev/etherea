import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap, take } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class HttpInterceptorService implements HttpInterceptor {
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
        return next.handle(request).pipe(
          catchError((err) => {
            if (err instanceof HttpErrorResponse) {
              switch (err.status) {
                case 403:
                  this.router.navigate(['forbidden']);
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

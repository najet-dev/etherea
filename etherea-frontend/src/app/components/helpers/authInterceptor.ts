import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { AuthService } from 'src/app/services/auth.service';
import { StorageService } from 'src/app/services/storage.service';

import { Observable, catchError, switchMap, take, throwError } from 'rxjs';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private storageService: StorageService,
    private router: Router
  ) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return this.authService.getCurrentUser().pipe(
      take(1),
      switchMap((user) => {
        const token = this.storageService.getToken();
        if (token) {
          // Clone the request and add the token to the headers
          request = request.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`,
            },
          });
        }
        return next.handle(request).pipe(
          catchError((err: HttpErrorResponse) => {
            if (err.status === 401) {
              // Redirect to login or handle unauthorized access
              this.router.navigate(['/signin']);
            }
            return throwError(() => err);
          })
        );
      })
    );
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ForgotPasswordRequest } from '../components/models/forgotPasswordRequest.model';
import { ForgotPasswordResponse } from '../components/models/forgotPasswordResponse.model';
import { ResetPasswordRequest } from '../components/models/resetPasswordRequest.model';
import { ResetPasswordResponse } from '../components/models/resetPasswordResponse.model';

@Injectable({
  providedIn: 'root',
})
export class PasswordResetService {
  apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  sendResetLink(
    request: ForgotPasswordRequest
  ): Observable<ForgotPasswordResponse> {
    return this.httpClient.post<ForgotPasswordResponse>(
      `${this.apiUrl}/resetToken/forgot-password`,
      request
    );
  }
  // Réinitialiser le mot de passe avec le token
  resetPassword(
    request: ResetPasswordRequest
  ): Observable<ResetPasswordResponse> {
    return this.httpClient.post<ResetPasswordResponse>(
      `${this.apiUrl}/resetToken/reset-password`,
      request
    );
  }
}

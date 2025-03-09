import { CookieChoice } from './cookie-choice.model';

export interface SaveCookieConsentRequest {
  userId: number;
  cookiePolicyVersion: string;
  cookieChoices: CookieChoice[];
}

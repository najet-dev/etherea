import { CookieChoice } from './cookie-choice.model';

export interface CookieConsent {
  id: number;
  userId: number;
  cookiePolicyVersion: string;
  consentDate: string;
  cookieChoices: CookieChoice[];
}

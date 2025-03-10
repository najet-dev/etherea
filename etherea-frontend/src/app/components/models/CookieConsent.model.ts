import { CookieChoice } from './cookie-choice.model';

export interface CookieConsent {
  id: number;
  userId: number | null;
  sessionId: string | null;
  cookiePolicyVersion: string;
  consentDate: string;
  cookieChoices: CookieChoice[];
}

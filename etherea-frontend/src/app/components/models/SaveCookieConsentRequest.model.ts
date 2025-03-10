import { CookieChoice } from './cookie-choice.model';

export interface SaveCookieConsentRequest {
  userId: number | null;
  sessionId: string | null;
  cookiePolicyVersion: string;
  cookieChoices: CookieChoice[];
}

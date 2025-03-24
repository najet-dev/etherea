import { CookieChoice } from './cookie-choice.model';
import { CookiePolicyVersion } from './cookiePolicyVersion.enum';

export interface SaveCookieConsentRequest {
  userId: number | null;
  sessionId: string | null;
  cookiePolicyVersion: CookiePolicyVersion;
  cookieChoices: CookieChoice[];
}

import { CookieChoice } from './cookie-choice.model';
import { CookiePolicyVersion } from './CookiePolicyVersion.enum';

export interface CookieConsent {
  id: number;
  userId: number | null;
  sessionId: string | null;
  cookiePolicyVersion: CookiePolicyVersion;
  consentDate: string;
  cookieChoices: CookieChoice[];
}

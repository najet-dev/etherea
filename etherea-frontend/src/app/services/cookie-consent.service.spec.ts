import { TestBed } from '@angular/core/testing';

import { CookieConsentService } from './cookie-consent.service';

describe('CookieConsentServiceService', () => {
  let service: CookieConsentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CookieConsentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

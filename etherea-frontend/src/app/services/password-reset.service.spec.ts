import { TestBed } from '@angular/core/testing';

import { PasswordResetService } from './password-reset.service';

describe('PasswordResetServiceService', () => {
  let service: PasswordResetService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PasswordResetService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

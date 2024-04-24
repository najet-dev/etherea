import { TestBed } from '@angular/core/testing';

import { LocalCartServiceService } from './local-cart-service.service';

describe('LocalCartServiceService', () => {
  let service: LocalCartServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocalCartServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

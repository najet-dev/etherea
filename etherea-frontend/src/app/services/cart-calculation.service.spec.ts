import { TestBed } from '@angular/core/testing';

import { CartCalculationService } from './cart-calculation.service';

describe('CartCalculationService', () => {
  let service: CartCalculationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CartCalculationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';

import { CartServiceFacade } from './cart-facade.service';

describe('CartServiceFacadeService', () => {
  let service: CartServiceFacade;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CartServiceFacade);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

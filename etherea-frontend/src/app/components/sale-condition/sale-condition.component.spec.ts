import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaleConditionComponent } from './sale-condition.component';

describe('SaleConditionComponent', () => {
  let component: SaleConditionComponent;
  let fixture: ComponentFixture<SaleConditionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SaleConditionComponent]
    });
    fixture = TestBed.createComponent(SaleConditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TipDetailComponent } from './tip-detail.component';

describe('TipDetailComponent', () => {
  let component: TipDetailComponent;
  let fixture: ComponentFixture<TipDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TipDetailComponent]
    });
    fixture = TestBed.createComponent(TipDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

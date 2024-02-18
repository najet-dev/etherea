import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DayCreamComponent } from './day-cream.component';

describe('DayCreamComponent', () => {
  let component: DayCreamComponent;
  let fixture: ComponentFixture<DayCreamComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DayCreamComponent]
    });
    fixture = TestBed.createComponent(DayCreamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

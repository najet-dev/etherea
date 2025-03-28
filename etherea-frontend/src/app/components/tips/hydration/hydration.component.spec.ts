import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HydrationComponent } from './hydration.component';

describe('HydrationComponent', () => {
  let component: HydrationComponent;
  let fixture: ComponentFixture<HydrationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HydrationComponent]
    });
    fixture = TestBed.createComponent(HydrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

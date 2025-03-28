import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolarProtectionComponent } from './solar-protection.component';

describe('SolarProtectionComponent', () => {
  let component: SolarProtectionComponent;
  let fixture: ComponentFixture<SolarProtectionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SolarProtectionComponent]
    });
    fixture = TestBed.createComponent(SolarProtectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

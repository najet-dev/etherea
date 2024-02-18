import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NightSensitiveComponent } from './night-sensitive.component';

describe('NightSensitiveComponent', () => {
  let component: NightSensitiveComponent;
  let fixture: ComponentFixture<NightSensitiveComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NightSensitiveComponent]
    });
    fixture = TestBed.createComponent(NightSensitiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

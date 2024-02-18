import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NightDryComponent } from './night-dry.component';

describe('NightDryComponent', () => {
  let component: NightDryComponent;
  let fixture: ComponentFixture<NightDryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NightDryComponent]
    });
    fixture = TestBed.createComponent(NightDryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

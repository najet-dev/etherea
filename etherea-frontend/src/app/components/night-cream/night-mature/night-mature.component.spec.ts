import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NightMatureComponent } from './night-mature.component';

describe('NightMatureComponent', () => {
  let component: NightMatureComponent;
  let fixture: ComponentFixture<NightMatureComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NightMatureComponent]
    });
    fixture = TestBed.createComponent(NightMatureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

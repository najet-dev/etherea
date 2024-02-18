import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NightNormalComponent } from './night-normal.component';

describe('NightNormalComponent', () => {
  let component: NightNormalComponent;
  let fixture: ComponentFixture<NightNormalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NightNormalComponent]
    });
    fixture = TestBed.createComponent(NightNormalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NightCreamComponent } from './night-cream.component';

describe('NightCreamComponent', () => {
  let component: NightCreamComponent;
  let fixture: ComponentFixture<NightCreamComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NightCreamComponent]
    });
    fixture = TestBed.createComponent(NightCreamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

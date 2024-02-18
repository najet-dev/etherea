import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NightCombinationComponent } from './night-combination.component';

describe('NightCombinationComponent', () => {
  let component: NightCombinationComponent;
  let fixture: ComponentFixture<NightCombinationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NightCombinationComponent]
    });
    fixture = TestBed.createComponent(NightCombinationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NightBlemishedComponent } from './night-blemished.component';

describe('NightBlemishedComponent', () => {
  let component: NightBlemishedComponent;
  let fixture: ComponentFixture<NightBlemishedComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NightBlemishedComponent]
    });
    fixture = TestBed.createComponent(NightBlemishedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

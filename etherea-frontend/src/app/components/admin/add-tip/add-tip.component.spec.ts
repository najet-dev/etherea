import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTipComponent } from './add-tip.component';

describe('AddTipComponent', () => {
  let component: AddTipComponent;
  let fixture: ComponentFixture<AddTipComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddTipComponent]
    });
    fixture = TestBed.createComponent(AddTipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateTipComponent } from './update-tip.component';

describe('UpdateTipComponent', () => {
  let component: UpdateTipComponent;
  let fixture: ComponentFixture<UpdateTipComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateTipComponent]
    });
    fixture = TestBed.createComponent(UpdateTipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreamComponent } from './cream.component';

describe('DayCreamComponent', () => {
  let component: CreamComponent;
  let fixture: ComponentFixture<CreamComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreamComponent],
    });
    fixture = TestBed.createComponent(CreamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccesComponent } from './access.component';

describe('ErrorsComponent', () => {
  let component: AccesComponent;
  let fixture: ComponentFixture<AccesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccesComponent],
    });
    fixture = TestBed.createComponent(AccesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

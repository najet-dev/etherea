import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalDataCharterComponent } from './personal-data-charter.component';

describe('PersonalDataCharterComponent', () => {
  let component: PersonalDataCharterComponent;
  let fixture: ComponentFixture<PersonalDataCharterComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PersonalDataCharterComponent]
    });
    fixture = TestBed.createComponent(PersonalDataCharterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

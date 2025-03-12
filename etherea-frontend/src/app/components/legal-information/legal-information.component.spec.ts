import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LegalInformationComponent } from './legal-information.component';

describe('LegalInformationComponent', () => {
  let component: LegalInformationComponent;
  let fixture: ComponentFixture<LegalInformationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LegalInformationComponent]
    });
    fixture = TestBed.createComponent(LegalInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

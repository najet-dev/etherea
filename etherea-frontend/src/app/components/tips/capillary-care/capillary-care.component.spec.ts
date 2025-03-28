import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CapillaryCareComponent } from './capillary-care.component';

describe('CapillaryCareComponent', () => {
  let component: CapillaryCareComponent;
  let fixture: ComponentFixture<CapillaryCareComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CapillaryCareComponent]
    });
    fixture = TestBed.createComponent(CapillaryCareComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

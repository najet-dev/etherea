import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SensitiveSkinComponent } from './sensitive-skin.component';

describe('SensitiveSkinComponent', () => {
  let component: SensitiveSkinComponent;
  let fixture: ComponentFixture<SensitiveSkinComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SensitiveSkinComponent]
    });
    fixture = TestBed.createComponent(SensitiveSkinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

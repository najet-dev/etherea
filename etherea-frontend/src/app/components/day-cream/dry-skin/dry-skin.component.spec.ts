import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrySkinComponent } from './dry-skin.component';

describe('DrySkinComponent', () => {
  let component: DrySkinComponent;
  let fixture: ComponentFixture<DrySkinComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DrySkinComponent]
    });
    fixture = TestBed.createComponent(DrySkinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

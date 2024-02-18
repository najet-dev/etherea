import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatureSkinComponent } from './mature-skin.component';

describe('MatureSkinComponent', () => {
  let component: MatureSkinComponent;
  let fixture: ComponentFixture<MatureSkinComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MatureSkinComponent]
    });
    fixture = TestBed.createComponent(MatureSkinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

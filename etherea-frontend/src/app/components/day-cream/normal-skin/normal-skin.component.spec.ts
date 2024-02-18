import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NormalSkinComponent } from './normal-skin.component';

describe('NormalSkinComponent', () => {
  let component: NormalSkinComponent;
  let fixture: ComponentFixture<NormalSkinComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NormalSkinComponent]
    });
    fixture = TestBed.createComponent(NormalSkinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

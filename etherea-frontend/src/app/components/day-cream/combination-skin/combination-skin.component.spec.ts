import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CombinationSkinComponent } from './combination-skin.component';

describe('CombinationSkinComponent', () => {
  let component: CombinationSkinComponent;
  let fixture: ComponentFixture<CombinationSkinComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CombinationSkinComponent]
    });
    fixture = TestBed.createComponent(CombinationSkinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

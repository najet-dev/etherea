import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlemishedSkinComponent } from './blemished-skin.component';

describe('BlemishedSkinComponent', () => {
  let component: BlemishedSkinComponent;
  let fixture: ComponentFixture<BlemishedSkinComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BlemishedSkinComponent]
    });
    fixture = TestBed.createComponent(BlemishedSkinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

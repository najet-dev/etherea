import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VolumeListComponent } from './volume-list.component';

describe('VolumeListComponent', () => {
  let component: VolumeListComponent;
  let fixture: ComponentFixture<VolumeListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VolumeListComponent]
    });
    fixture = TestBed.createComponent(VolumeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

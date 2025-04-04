import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateVolumeComponent } from './update-volume.component';

describe('UpdateVolumeComponent', () => {
  let component: UpdateVolumeComponent;
  let fixture: ComponentFixture<UpdateVolumeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateVolumeComponent]
    });
    fixture = TestBed.createComponent(UpdateVolumeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

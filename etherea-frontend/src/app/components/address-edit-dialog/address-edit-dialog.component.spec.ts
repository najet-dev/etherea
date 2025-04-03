import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddressEditDialogComponent } from './address-edit-dialog.component';

describe('AddressEditDialogComponent', () => {
  let component: AddressEditDialogComponent;
  let fixture: ComponentFixture<AddressEditDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddressEditDialogComponent]
    });
    fixture = TestBed.createComponent(AddressEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

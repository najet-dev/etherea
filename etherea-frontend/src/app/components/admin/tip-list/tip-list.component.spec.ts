import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TipListComponent } from './tip-list.component';

describe('TipListComponent', () => {
  let component: TipListComponent;
  let fixture: ComponentFixture<TipListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TipListComponent]
    });
    fixture = TestBed.createComponent(TipListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

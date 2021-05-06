import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NptComponent } from './npt.component';

describe('NptComponent', () => {
  let component: NptComponent;
  let fixture: ComponentFixture<NptComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NptComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

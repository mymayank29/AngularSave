import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NptEventDetailsComponent } from './npt-event-details.component';

describe('NptEventDetailsComponent', () => {
  let component: NptEventDetailsComponent;
  let fixture: ComponentFixture<NptEventDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NptEventDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NptEventDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

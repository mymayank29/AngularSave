import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WellTimeLogComponent } from './well-time-log.component';

describe('WellTimeLogComponent', () => {
  let component: WellTimeLogComponent;
  let fixture: ComponentFixture<WellTimeLogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WellTimeLogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WellTimeLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

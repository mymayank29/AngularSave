import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BannnerCommingSoonComponent } from './bannner-comming-soon.component';

describe('BannnerCommingSoonComponent', () => {
  let component: BannnerCommingSoonComponent;
  let fixture: ComponentFixture<BannnerCommingSoonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BannnerCommingSoonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BannnerCommingSoonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

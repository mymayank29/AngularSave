import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AffectedInvoicesComponent } from './affected-invoices.component';

describe('AffectedInvoicesComponent', () => {
  let component: AffectedInvoicesComponent;
  let fixture: ComponentFixture<AffectedInvoicesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AffectedInvoicesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AffectedInvoicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractRestrictionsComponent } from './contract-restrictions.component';
import { SharedModule } from '../../shared/shared.module';
import { AppRoutingModule } from '../../app-routing.module';
import { InvoicesComponent } from '../../invoices/invoices.component';
import { InvoiceTableComponent } from '../../invoices/invoice-table/invoice-table.component';

describe('ContractRestrictionsComponent', () => {
  let component: ContractRestrictionsComponent;
  let fixture: ComponentFixture<ContractRestrictionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule
      ],
      declarations: [
         InvoicesComponent,
         ContractRestrictionsComponent,
         InvoiceTableComponent
      ]
    }).compileComponents();
  }));


  it('should created', () => {
    fixture = TestBed.createComponent(ContractRestrictionsComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});

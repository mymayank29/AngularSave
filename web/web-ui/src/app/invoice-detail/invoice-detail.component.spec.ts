import { LineItemsComponent } from './line-items/line-items.component';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceDetailComponent } from './invoice-detail.component';
import { SharedModule } from '../shared/shared.module';
import { InvoicesInDetailComponent } from './invoices-in-detail/invoices-in-detail.component';
import { ReviewComponent } from './review/review.component';
import { NptEventsComponent } from './npt-events/npt-events.component';
import { ContractRestrictionsComponent } from './contract-restrictions/contract-restrictions.component';
import { AppRoutingModule } from '../app-routing.module';
import { InvoicesComponent } from '../invoices/invoices.component';
import { InvoicesModule } from '../invoices/invoices.module';
import { MainStoreModule } from '../+store/main-store.module';
import { HttpClientModule } from '@angular/common/http';

describe('InvoiceDetailComponent', () => {

  let component: InvoiceDetailComponent;
  let fixture: ComponentFixture<InvoiceDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule,
        InvoicesModule,
        MainStoreModule,
        HttpClientModule
      ],
      declarations: [
        InvoiceDetailComponent,
        InvoicesInDetailComponent,
        LineItemsComponent,
        ReviewComponent,
        NptEventsComponent,
        ContractRestrictionsComponent
      ]
    })
    .compileComponents();
  }));

  it('should create', () => {
    fixture = TestBed.createComponent(InvoiceDetailComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});

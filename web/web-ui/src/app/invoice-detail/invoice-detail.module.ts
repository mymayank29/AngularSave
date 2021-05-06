import { SharedModule } from './../shared/shared.module';
import { MaterialModule } from './../shared/material/material.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InvoiceDetailComponent } from './invoice-detail.component';
import { InvoiceDetailRoutingModule } from './invoice-detail-routing.module';
import { InvoicesInDetailComponent } from './invoices-in-detail/invoices-in-detail.component';
import { LineItemsComponent } from './line-items/line-items.component';
import { NptEventsComponent } from './npt-events/npt-events.component';
import { ContractRestrictionsComponent } from './contract-restrictions/contract-restrictions.component';
import { ReviewComponent } from './review/review.component';
import { StoreModule } from '@ngrx/store';
import { invoicesDetailsReducer } from '../+store/invoices-details/invoices-details.reducer';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    MaterialModule,
    InvoiceDetailRoutingModule,
    StoreModule.forFeature('invoicesDetails', invoicesDetailsReducer),
    SharedModule
  ],
  declarations: [
    InvoiceDetailComponent,
    InvoicesInDetailComponent,
    LineItemsComponent,
    NptEventsComponent,
    ContractRestrictionsComponent,
    ReviewComponent
  ]
})
export class InvoiceDetailModule { }

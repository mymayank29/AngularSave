import { FlaggedInvoicesEffects } from './../+store/flagged-invoices/flagged-invoices.effects';
import { EffectsModule } from '@ngrx/effects';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SupplierReportComponent } from './supplier-report.component';

import { SupplierReportRoutingModule } from './supplier-report-routing.module';
import { SupplierReportTableComponent } from './supplier-report-table/supplier-report-table.component';
import { StoreModule } from '@ngrx/store';
import { filtersReducer, togglesReducer } from '../+store';
import { flaggedInvoicesReducer } from '../+store/flagged-invoices/flagged-invoices.reducer';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    SupplierReportRoutingModule,
    SharedModule,
    StoreModule.forFeature('filters', filtersReducer),
    StoreModule.forFeature('toggles', togglesReducer),
    StoreModule.forFeature('flaggedInvoices', flaggedInvoicesReducer),
    EffectsModule.forFeature([FlaggedInvoicesEffects])
  ],
  declarations: [
    SupplierReportComponent,
    SupplierReportTableComponent
  ]
})
export class SupplierReportModule { }

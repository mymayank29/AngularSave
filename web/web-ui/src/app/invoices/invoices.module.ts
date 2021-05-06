import { NgModule } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { InvoicesComponent } from './invoices.component';
import { InvoiceTableComponent } from './invoice-table/invoice-table.component';
import { CountComponent } from '../count/count.component';


import { StoreModule } from '@ngrx/store';
import { InvoicesEffects, invoicesReducer, filtersReducer } from '../+store';
import { EffectsModule } from '@ngrx/effects';
import { SharedModule } from '../shared/shared.module';
import { togglesReducer } from '../+store/toggles/toggles.reducer';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature('invoices', invoicesReducer),
    StoreModule.forFeature('filters', filtersReducer),
    StoreModule.forFeature('toggles', togglesReducer),
    EffectsModule.forFeature([InvoicesEffects]),
    SharedModule
  ],
  declarations: [
    InvoicesComponent,
    InvoiceTableComponent
  ],
  providers: [DecimalPipe]
})
export class InvoicesModule { }

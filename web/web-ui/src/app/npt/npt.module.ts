import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NptRoutingModule } from './npt-routing.module';
import { NptComponent } from './npt.component';
import { AffectedInvoicesComponent } from './affected-invoices/affected-invoices.component';
import { WellTimeLogComponent } from './well-time-log/well-time-log.component';
import { NptEventDetailsComponent } from './npt-event-details/npt-event-details.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    NptRoutingModule,
    SharedModule
  ],
  declarations: [
    NptComponent,
    AffectedInvoicesComponent,
    WellTimeLogComponent,
    NptEventDetailsComponent
  ]
})
export class NptModule { }

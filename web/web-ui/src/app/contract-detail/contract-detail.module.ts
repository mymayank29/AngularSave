import { ContractDetailRoutingModule } from './contract-detail-routing.module';
import { ContractDetailComponent } from './contract-detail.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    ContractDetailRoutingModule
  ],
  declarations: [ContractDetailComponent]
})
export class ContractDetailModule { }

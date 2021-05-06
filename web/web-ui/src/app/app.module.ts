import { ContractDetailModule } from './contract-detail/contract-detail.module';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { NoopAnimationsModule  } from '@angular/platform-browser/animations';
import 'hammerjs';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { InvoicesModule } from './invoices/invoices.module';
import { MainStoreModule } from './+store/main-store.module';
import { SharedModule } from './shared/shared.module';
import { ContractDetailComponent } from './contract-detail/contract-detail.component';

import { DatePipe } from '@angular/common';
import { CountComponent } from './count/count.component';
@NgModule({
  declarations: [
    AppComponent
    ,CountComponent
  ],
  imports: [
    BrowserModule,
    NoopAnimationsModule,
    HttpClientModule,
    SharedModule,
    InvoicesModule,
    MainStoreModule,
    AppRoutingModule,
    ContractDetailModule
  ],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }

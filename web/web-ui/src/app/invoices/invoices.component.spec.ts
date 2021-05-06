import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoicesComponent } from './invoices.component';
import { InvoiceTableComponent } from './invoice-table/invoice-table.component';
import { EffectsModule, Actions } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { SharedModule } from '../shared/shared.module';
import { HttpClientModule } from '@angular/common/http';
import { MainStoreModule } from '../+store/main-store.module';
import { AppRoutingModule } from '../app-routing.module';

describe('InvoicesComponent', () => {

  let component: InvoicesComponent;
  let fixture: ComponentFixture<InvoicesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        SharedModule,
        HttpClientModule,
        MainStoreModule,
        AppRoutingModule
      ],
      declarations: [
        InvoicesComponent, InvoiceTableComponent]
    }).compileComponents();
  }));

  it('#InvoiceComponent should create', () => {
    fixture = TestBed.createComponent(InvoicesComponent);
    component = fixture.debugElement.componentInstance;
    expect(component).toBeTruthy();
  });
});

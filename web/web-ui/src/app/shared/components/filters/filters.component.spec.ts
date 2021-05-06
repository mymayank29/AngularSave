import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FiltersComponent } from './filters.component';
import { ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { AppRoutingModule } from '../../../app-routing.module';
import { SharedModule } from '../../shared.module';
import { InvoicesComponent } from '../../../invoices/invoices.component';
import { InvoiceTableComponent } from '../../../invoices/invoice-table/invoice-table.component';
import { MainStoreModule } from '../../../+store/main-store.module';
import { HttpClientModule } from '@angular/common/http';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('FiltersComponent', () => {
  let component: FiltersComponent;
  let fixture: ComponentFixture<FiltersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule,
        NoopAnimationsModule,
        MainStoreModule,
        HttpClientModule
      ],
      declarations: [
        InvoicesComponent,
        InvoiceTableComponent
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

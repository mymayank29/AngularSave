import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierReportComponent } from './supplier-report.component';
import { SharedModule } from '../shared/shared.module';
import { SupplierReportTableComponent } from './supplier-report-table/supplier-report-table.component';
import { MainStoreModule } from '../+store/main-store.module';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from '../app-routing.module';
import { InvoicesModule } from '../invoices/invoices.module';

describe('SupplierReportComponent', () => {

  let component: SupplierReportComponent;
  let fixture: ComponentFixture<SupplierReportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        MainStoreModule,
        HttpClientModule,
        AppRoutingModule,
        InvoicesModule
      ],
      declarations: [
        SupplierReportTableComponent,
        SupplierReportComponent ]
    })
    .compileComponents();
  }));

  it('should create', () => {
    fixture = TestBed.createComponent(SupplierReportComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});

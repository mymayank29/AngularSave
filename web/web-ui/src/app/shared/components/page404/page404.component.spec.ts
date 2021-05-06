import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Page404Component } from './page404.component';
import { SharedModule } from '../../shared.module';
import { AppRoutingModule } from '../../../app-routing.module';
import { InvoicesComponent } from '../../../invoices/invoices.component';
import { InvoiceTableComponent } from '../../../invoices/invoice-table/invoice-table.component';

describe('Page404Component', () => {
  let component: Page404Component;
  let fixture: ComponentFixture<Page404Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule,
        // InvoicesModule,
        // MainStoreModule,
        // HttpClientModule
      ],
      declarations: [
        InvoicesComponent,
        InvoiceTableComponent
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Page404Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

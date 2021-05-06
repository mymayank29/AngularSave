import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceTableComponent } from './invoice-table.component';
import { AppRoutingModule } from '../../app-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { InvoicesModule } from '../invoices.module';
import { InvoicesComponent } from '../invoices.component';
import { ConvertToCSVService } from '../../shared/services/convert-to-csv.service';

describe('InvoiceTableComponent', () => {

  let component: InvoiceTableComponent;
  let fixture: ComponentFixture<InvoiceTableComponent>;
  let convertToCSVService: ConvertToCSVService;
  let spy: jasmine.Spy;
  let csvMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule
      ],
      declarations: [
        InvoiceTableComponent,
        InvoicesComponent
      ],
      providers: [ConvertToCSVService]
    })
    .compileComponents();
  }));
  beforeEach(() => {
    fixture = TestBed.createComponent(InvoiceTableComponent);
    component = fixture.componentInstance;
    convertToCSVService = fixture.debugElement.injector.get(ConvertToCSVService);
    csvMock = ['id', 'ariba_doc_id'];
    spy = spyOn(convertToCSVService, 'convertJsonToCSV').and.returnValue(csvMock);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

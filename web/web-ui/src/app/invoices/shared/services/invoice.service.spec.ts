import { TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { InvoiceService } from './invoice.service';
import { HttpParams } from '@angular/common/http';
import { of } from 'rxjs';

describe('InvoiceService', () => {
  // We declare the variables that we'll use for the Test Controller and for our Service
  let httpMock: HttpTestingController;
  let invoiceService: InvoiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [InvoiceService]
    });

    // We inject our service (which imports the HttpClient) and the Test Controller
    httpMock = TestBed.get(HttpTestingController);
    invoiceService = TestBed.get(InvoiceService);
  });

  afterEach(() => {
    httpMock.verify();
  });


  it('should be created', () => {
    expect(invoiceService).toBeTruthy();
  });

  it('returned observable should the match the right data', () => {
    const expectedData = {id: '111'};
    invoiceService.getInvoiceById('1').subscribe(data => {
      expect(data.id).toEqual('111');
    });
    const req = httpMock.expectOne(`${invoiceService.url}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(expectedData);
  });
});


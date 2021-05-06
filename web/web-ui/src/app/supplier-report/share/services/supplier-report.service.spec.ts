import { TestBed } from '@angular/core/testing';

import { SupplierReportService } from './supplier-report.service';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';

describe('SupplierReportService', () => {

  let httpTestingController: HttpTestingController;
  let service: SupplierReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SupplierReportService]
  });
    // We inject our service (which imports the HttpClient) and the Test Controller
  httpTestingController = TestBed.get(HttpTestingController);
  service = TestBed.get(SupplierReportService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

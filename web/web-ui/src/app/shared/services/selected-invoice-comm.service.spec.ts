import { TestBed } from '@angular/core/testing';

import { SelectedInvoiceCommService } from './selected-invoice-comm.service';

describe('SelectedInvoiceCommService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SelectedInvoiceCommService = TestBed.get(SelectedInvoiceCommService);
    expect(service).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';

import { InvoicesInDetailCommService } from './invoices-in-detail-comm.service';

describe('InvoicesInDetailCommService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: InvoicesInDetailCommService = TestBed.get(InvoicesInDetailCommService);
    expect(service).toBeTruthy();
  });
});

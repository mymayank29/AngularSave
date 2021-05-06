import { InvoiceDetailModule } from './invoice-detail.module';

describe('InvoiceDetailModule', () => {
  let invoiceDetailModule: InvoiceDetailModule;

  beforeEach(() => {
    invoiceDetailModule = new InvoiceDetailModule();
  });

  it('should create an instance', () => {
    expect(invoiceDetailModule).toBeTruthy();
  });
});

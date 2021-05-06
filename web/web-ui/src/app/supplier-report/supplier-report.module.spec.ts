import { SupplierReportModule } from './supplier-report.module';

describe('SupplierReportModule', () => {
  let supplierReportModule: SupplierReportModule;

  beforeEach(() => {
    supplierReportModule = new SupplierReportModule();
  });

  it('should create an instance', () => {
    expect(supplierReportModule).toBeTruthy();
  });
});

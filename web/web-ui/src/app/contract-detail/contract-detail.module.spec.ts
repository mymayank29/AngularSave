import { ContractDetailModule } from './contract-detail.module';

describe('ContractDetailModule', () => {
  let contractDetailModule: ContractDetailModule;

  beforeEach(() => {
    contractDetailModule = new ContractDetailModule();
  });

  it('should create an instance', () => {
    expect(contractDetailModule).toBeTruthy();
  });
});

import { ContractDetailRoutingModule } from './contract-detail-routing.module';

describe('ContractDetailRoutingModule', () => {
  let contractDetailRoutingModule: ContractDetailRoutingModule;

  beforeEach(() => {
    contractDetailRoutingModule = new ContractDetailRoutingModule();
  });

  it('should create an instance', () => {
    expect(contractDetailRoutingModule).toBeTruthy();
  });
});

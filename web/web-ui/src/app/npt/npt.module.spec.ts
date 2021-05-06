import { NptModule } from './npt.module';

describe('NptModule', () => {
  let nptModule: NptModule;

  beforeEach(() => {
    nptModule = new NptModule();
  });

  it('should create an instance', () => {
    expect(nptModule).toBeTruthy();
  });
});

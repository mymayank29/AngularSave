import { MainStoreModule } from './main-store.module';

describe('MainStoreModule', () => {
  let mainStoreModule: MainStoreModule;

  beforeEach(() => {
    mainStoreModule = new MainStoreModule();
  });

  it('should create an instance', () => {
    expect(mainStoreModule).toBeTruthy();
  });
});

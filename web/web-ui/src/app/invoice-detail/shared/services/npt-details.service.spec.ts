import { TestBed } from '@angular/core/testing';

import { NptDetailsService } from './npt-details.service';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';

describe('NptDetailsService', () => {

  let httpTestingController: HttpTestingController;
  let service: NptDetailsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NptDetailsService]
  });
    // We inject our service (which imports the HttpClient) and the Test Controller
  httpTestingController = TestBed.get(HttpTestingController);
  service = TestBed.get(NptDetailsService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

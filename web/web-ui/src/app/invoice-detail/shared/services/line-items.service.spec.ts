import { TestBed } from '@angular/core/testing';

import { LineItemsService } from './line-items.service';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';

describe('LineItemsService', () => {

  let httpTestingController: HttpTestingController;
  let service: LineItemsService;

  beforeEach(() => {
     TestBed.configureTestingModule({
    imports: [HttpClientTestingModule],
    providers: [LineItemsService]
  });

  httpTestingController = TestBed.get(HttpTestingController);
  service = TestBed.get(LineItemsService);
});

afterEach(() => {
  httpTestingController.verify();
});

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

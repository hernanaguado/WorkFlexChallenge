import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { WORKATIONS } from './workation.fixtures';
import { Workation } from './workation.model';
import { WorkationService } from './workation.service';

const WORKATION_URL = `${environment.apiUrl}/workation`;

describe('WorkationService', () => {
  let service: WorkationService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(WorkationService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should GET all workations from the workation endpoint', () => {
    // given
    let result: Workation[] | undefined;

    // when
    service.getAll().subscribe((workations) => (result = workations));

    // then
    const request = httpTesting.expectOne(WORKATION_URL);
    expect(request.request.method).toBe('GET');
    request.flush(WORKATIONS);

    expect(result).toEqual(WORKATIONS);
  });

  it('should propagate HTTP errors to the subscriber', () => {
    // given
    let failed = false;
    service.getAll().subscribe({ error: () => (failed = true) });

    // when
    httpTesting
      .expectOne(WORKATION_URL)
      .flush('Server error', { status: 500, statusText: 'Internal Server Error' });

    // then
    expect(failed).toBe(true);
  });
});
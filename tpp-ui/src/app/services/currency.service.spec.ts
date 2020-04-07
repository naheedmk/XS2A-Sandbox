import { TestBed } from '@angular/core/testing';

import { CurrencyService } from './currency.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('CurrencyService', () => {
  let currencyService: CurrencyService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CurrencyService],
    });
    currencyService = TestBed.get(CurrencyService);
    httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    const service: CurrencyService = TestBed.get(CurrencyService);
    expect(service).toBeTruthy();
  });

  it('should get supported Currencies', () => {
    currencyService.getSupportedCurrencies();
    httpMock.verify();
  });
});

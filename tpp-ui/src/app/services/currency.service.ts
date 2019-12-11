import { Injectable } from '@angular/core';
import {of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {

  private currencies: Array<string> = [];

  constructor() {
    this.initializeCurrenciesList();
  }

  initializeCurrenciesList() {
    return this.getSupportedCurrencies().subscribe(
      data => {
        this.currencies = data;
        console.log("data from service " + data);
      },
      error => console.log(error)
    )
  }

  get currencyList() {
    return this.currencies;
  }

  getSupportedCurrencies() {
    //return this.http.get(this.url + '/tpp/currencies');
    return of(
      [
        'EUR',
        'UAH',
        'USD'
      ]
    )
  }

}

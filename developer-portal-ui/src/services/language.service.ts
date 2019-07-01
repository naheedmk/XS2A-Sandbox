import { Injectable } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  lang = 'ua';
  langs: string[] = ['en', 'ua', 'es', 'de'];

  constructor(
    private translateService: TranslateService
  ) { }

  initializeTranslation() {
    this.translateService.addLangs(this.langs);
    this.translateService.setDefaultLang(this.lang);
    this.translateService.use(this.lang);
  }

  setLang(lang: string) {
    this.translateService.use(lang);
    this.lang = lang;
  }

  getLang() {
    return this.lang;
  }
}

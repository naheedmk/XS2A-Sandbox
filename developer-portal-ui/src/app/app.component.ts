import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../services/data.service';
import { LoginService } from '../services/login.service';
import {
  CustomizeService,
  GlobalSettings,
  Theme,
} from '../services/customize.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  globalSettings: GlobalSettings;
  lang = 'en';
  langs: string[] = ['en', 'ua', 'es', 'de'];
  langIcons: object = {
    en: '../assets/icons/united-kingdom.png',
    de: '../assets/icons/germany.png',
    es: '../assets/icons/spain.png',
    ua: '../assets/icons/ukraine.png'
  };
  private langCollapsed: boolean;

  constructor(
    private router: Router,
    private actRoute: ActivatedRoute,
    public dataService: DataService,
    public loginService: LoginService,
    public customizeService: CustomizeService,
    private translateService: TranslateService
  ) {
    this.initializeTranslation();
    this.langCollapsed = true;
  }

  goToPage(page) {
    this.router.navigateByUrl(`/${page}`);
  }

  onActivate(ev) {
    this.dataService.currentRouteUrl = this.actRoute[
      '_routerState'
    ].snapshot.url;
  }

  initializeTranslation() {
    this.translateService.addLangs(this.langs);
    this.translateService.setDefaultLang('en');
    this.translateService.use('en');
  }

  changeLang(lang: string) {
    this.translateService.use(lang);
    this.lang = lang;
    this.collapseThis();
  }

  collapseThis() {
    this.langCollapsed = !this.langCollapsed;
  }

  ngOnInit() {
    let theme: Theme;
    this.customizeService.getJSON().then(data => {
      theme = data;
      this.customizeService.changeFontFamily(theme.globalSettings.fontFamily);
      this.globalSettings = theme.globalSettings;
      if (theme.globalSettings.logo.indexOf('/') === -1) {
        theme.globalSettings.logo =
          '../assets/UI' +
          (this.customizeService.isCustom() ? '/custom/' : '/') +
          theme.globalSettings.logo;
      }
      if (theme.contactInfo.img.indexOf('/') === -1) {
        theme.contactInfo.img =
          '../assets/UI' +
          (this.customizeService.isCustom() ? '/custom/' : '/') +
          theme.contactInfo.img;
      }
      this.customizeService.setUserTheme(theme);
    });
  }
}

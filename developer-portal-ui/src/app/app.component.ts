import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../services/data.service';
import { LoginService } from '../services/login.service';
import {
  CustomizeService,
  GlobalSettings,
} from '../services/customize.service';
import { LocalStorageService } from '../services/local-storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  globalSettings: GlobalSettings;
  defaultSettings: GlobalSettings;
  headerBG: string;
  headerFontColor: string;
  footerBG: string;
  footerFontColor: string;
  previousStatus = false;

  constructor(
    private router: Router,
    private actRoute: ActivatedRoute,
    public dataService: DataService,
    public loginService: LoginService,
    public customizeService: CustomizeService,
    private localStorageService: LocalStorageService
  ) {}

  goToPage(page) {
    this.router.navigateByUrl(`/${page}`);
  }

  onActivate(ev) {
    this.dataService.currentRouteUrl = this.actRoute[
      '_routerState'
    ].snapshot.url;
  }

  ngOnInit() {
    let theme;
    if (!this.localStorageService.get('defaultTheme')) {
      theme = this.customizeService.getTheme();
      this.localStorageService.set('defaultTheme', theme);
    }
    if (this.localStorageService.get('userTheme')) {
      theme = this.localStorageService.get('userTheme');
      this.customizeService.setUserTheme(theme);
    } else {
      theme = this.localStorageService.get('defaultTheme');
    }
    this.customizeService.changeFontFamily();
    this.globalSettings = theme.globalSettings;
    this.defaultSettings = this.customizeService.getDefaultTheme().globalSettings;

    this.headerBG = this.defaultSettings.headerBG;
    this.headerFontColor = this.defaultSettings.headerFontColor;
    this.footerBG = this.defaultSettings.footerBG;
    this.footerFontColor = this.defaultSettings.footerFontColor;

    setInterval(() => {
      if (this.customizeService.getChangeStatus() !== this.previousStatus) {
        console.log('works');
        this.previousStatus = this.customizeService.getChangeStatus();
        theme = this.localStorageService.get('userTheme')
          ? this.localStorageService.get('userTheme')
          : this.customizeService.getNewThemaStatus()
          ? this.customizeService.getTheme()
          : this.localStorageService.get('defaultTheme');
        this.globalSettings = theme.globalSettings;
        if (this.globalSettings.headerBG) {
          this.headerBG = this.globalSettings.headerBG;
          this.headerFontColor = this.globalSettings.headerFontColor;
        }
        if (this.globalSettings.footerBG) {
          this.footerBG = this.globalSettings.footerBG;
          this.footerFontColor = this.globalSettings.footerFontColor;
        }
      }
    }, 500);
  }
}

import { Injectable } from '@angular/core';
import { LocalStorageService } from './local-storage.service';

@Injectable({
  providedIn: 'root',
})
export class CustomizeService {
  private NEW_THEME_WAS_SET = false;
  private STATUS_WAS_CHANGED = false;
  private DEFAULT_THEME: Theme = {
    globalSettings: {
      logoPath: '../assets/images/Logo_XS2ASandbox.png',
      fontFamily: 'Arial, sans-serif',
      headerBG: '#ffffff',
      headerFontColor: '#000000',
      footerBG: '#054f72',
      footerFontColor: '#ffffff',
      facebook: 'https://www.facebook.com/adorsysGmbH/',
      linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
    },
    contactInfo: {
      imgPath: '../../assets/images/Rene.png',
      name: 'René Pongratz',
      position: 'Software Architect & Expert for API Management',
      email: 'psd2@adorsys.de',
    },
    officesInfo: [
      {
        city: 'Nürnberg',
        company: 'adorsys GmbH & Co. KG',
        addressFirstLine: 'Fürther Str. 246a, Gebäude 32 im 4.OG',
        addressSecondLine: '90429 Nürnberg',
        phone: '+49(0)911 360698-0',
        email: 'psd2@adorsys.de',
      },
      {
        city: 'Frankfurt',
        company: 'adorsys GmbH & Co. KG',
        addressFirstLine: 'Frankfurter Straße 63 - 69',
        addressSecondLine: '65760 Eschborn',
        email: 'frankfurt@adorsys.de',
        facebook: 'https://www.facebook.com/adorsysGmbH/',
        linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
      },
    ],
  };
  private USER_THEME: Theme = {
    globalSettings: {
      logoPath: '',
      fontFamily: '',
    },
    contactInfo: {
      imgPath: '',
      name: '',
      position: '',
    },
    officesInfo: [
      {
        city: '',
        company: '',
        addressFirstLine: '',
        addressSecondLine: '',
      },
      {
        city: '',
        company: '',
        addressFirstLine: '',
        addressSecondLine: '',
      },
    ],
  };

  constructor(private localStorageService: LocalStorageService) {}

  getTheme() {
    if (this.NEW_THEME_WAS_SET) {
      return this.USER_THEME;
    } else {
      return this.DEFAULT_THEME;
    }
  }

  getDefaultTheme() {
    this.STATUS_WAS_CHANGED = !this.STATUS_WAS_CHANGED;
    return this.DEFAULT_THEME;
  }

  setDefaultTheme() {
    this.NEW_THEME_WAS_SET = false;
    this.STATUS_WAS_CHANGED = !this.STATUS_WAS_CHANGED;
    this.changeFontFamily();
    this.USER_THEME = {
      globalSettings: {
        logoPath: '',
        fontFamily: '',
      },
      contactInfo: {
        imgPath: '',
        name: '',
        position: '',
      },
      officesInfo: [
        {
          city: '',
          company: '',
          addressFirstLine: '',
          addressSecondLine: '',
        },
        {
          city: '',
          company: '',
          addressFirstLine: '',
          addressSecondLine: '',
        },
      ],
    };
  }

  setUserTheme(theme: Theme) {
    this.USER_THEME = theme;
    this.changeFontFamily(this.USER_THEME.globalSettings.fontFamily);
    this.NEW_THEME_WAS_SET = true;
    this.STATUS_WAS_CHANGED = !this.STATUS_WAS_CHANGED;
  }

  getLogo() {
    if (this.NEW_THEME_WAS_SET) {
      return this.USER_THEME.globalSettings.logoPath;
    } else {
      return this.DEFAULT_THEME.globalSettings.logoPath;
    }
  }

  changeFontFamily(value?: string) {
    if (value) {
      this.USER_THEME.globalSettings.fontFamily = value;
      const body = document.getElementsByTagName('body');
      body[0].setAttribute(
        'style',
        `font-family: ${this.USER_THEME.globalSettings.fontFamily}`
      );
    } else {
      const body = document.getElementsByTagName('body');
      body[0].setAttribute(
        'style',
        `font-family: ${this.DEFAULT_THEME.globalSettings.fontFamily}`
      );
    }
  }

  getChangeStatus() {
    return this.STATUS_WAS_CHANGED;
  }

  getNewThemaStatus() {
    return this.NEW_THEME_WAS_SET;
  }
}

export interface Theme {
  globalSettings: GlobalSettings;
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];
}

export interface GlobalSettings {
  logoPath: string;
  fontFamily?: string;
  headerBG?: string;
  headerFontColor?: string;
  footerBG?: string;
  footerFontColor?: string;
  facebook?: string;
  linkedIn?: string;
}

export interface ContactInfo {
  imgPath: string;
  name: string;
  position: string;
  email?: string;
  phone?: string;
}

export interface OfficeInfo {
  city: string;
  company: string;
  addressFirstLine: string;
  addressSecondLine: string;
  phone?: string;
  email?: string;
  facebook?: string;
  linkedIn?: string;
}

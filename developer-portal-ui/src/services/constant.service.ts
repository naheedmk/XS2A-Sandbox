import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ConstantService {
  private LOGO_PATH = '../assets/images/Logo_XS2ASandbox.png';
  private FONT_FAMILY = '';

  getLogo() {
    return this.LOGO_PATH;
  }

  setLogo(value: string) {
    this.LOGO_PATH = value;
  }
}

import { Component } from '@angular/core';
import { ConstantService } from '../../services/constant.service';

@Component({
  selector: 'app-settings-modal',
  templateUrl: './settings-modal.component.html',
  styleUrls: ['./settings-modal.component.scss'],
})
export class SettingsModalComponent {
  openFlag: boolean;
  newLogoPath: string;

  constructor(private constantService: ConstantService) {
    this.newLogoPath = '';
  }

  close() {
    this.openFlag = false;
  }

  save() {
    this.constantService.setLogo(this.newLogoPath);
    this.collapseThis();
  }

  collapseThis() {
    const collapsibleItemContent = document.getElementById('modal');

    if (collapsibleItemContent.style.maxHeight) {
      collapsibleItemContent.style.maxHeight = '';
    } else {
      collapsibleItemContent.style.maxHeight = '20vw';
    }
  }
}

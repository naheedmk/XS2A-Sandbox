import { Component } from '@angular/core';
import {
  ContactInfo,
  CustomizeService,
} from '../../services/customize.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  today = +new Date();
  contactInfo: ContactInfo;

  constructor(private customizeService: CustomizeService) {
    setInterval(() => {
      this.contactInfo = this.customizeService.getTheme().contactInfo;
    }, 100);
  }

  checkTodayDay(date) {
    let isToday = false;
    if (date > this.today) {
      isToday = true;
    }
    return isToday;
  }
}

import { Component, OnInit } from '@angular/core';
import {
  ContactInfo,
  CustomizeService,
  OfficeInfo,
} from '../../services/customize.service';
import { LocalStorageService } from '../../services/local-storage.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss'],
})
export class ContactComponent implements OnInit {
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];
  previousStatus = false;

  constructor(
    public customizeService: CustomizeService,
    private localStorageService: LocalStorageService
  ) {}

  ngOnInit() {
    let theme = this.customizeService.getTheme();
    this.contactInfo = theme.contactInfo;
    this.officesInfo = theme.officesInfo;
    setInterval(() => {
      if (this.customizeService.getChangeStatus() !== this.previousStatus) {
        console.log('works');
        this.previousStatus = this.customizeService.getChangeStatus();
        theme = this.localStorageService.get('userTheme')
          ? this.localStorageService.get('userTheme')
          : this.customizeService.getNewThemaStatus()
          ? this.customizeService.getTheme()
          : this.localStorageService.get('defaultTheme');
        this.contactInfo = theme.contactInfo;
        this.officesInfo = theme.officesInfo;
      }
    }, 500);
  }
}

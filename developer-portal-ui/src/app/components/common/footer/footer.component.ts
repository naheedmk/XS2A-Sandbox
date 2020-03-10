import {Component, Input, OnInit} from '@angular/core';
import {NavigationService} from "../../../services/navigation.service";

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  @Input() navigation;
  @Input() globalSettings;
  @Input() logoLink;
  socialMedia = [];

  private supportedSocialMedia = {
    twitter: "fa-twitter",
    facebook: "fa-facebook-f",
    linkedin: "fa-linkedin-in",
    instagram: "fa-instagram",
    youtube: "fa-youtube",
    xing: "fa-xing"
  };
  private defaultLogoLink = '/home';

  constructor(private navigationService: NavigationService) {
  }

  ngOnInit() {
    if (this.globalSettings.socialMedia) {
      this.socialMedia = Object.keys(this.globalSettings.socialMedia);
    }
  }

  getIconForSocialMedia(social: any) {
    return `social-media-icon fab ${this.supportedSocialMedia[social]}`;
  }
}

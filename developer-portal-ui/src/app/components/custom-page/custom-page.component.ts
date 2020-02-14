import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../services/language.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-custom-page',
  templateUrl: './custom-page.component.html',
  styleUrls: ['./custom-page.component.scss']
})
export class CustomPageComponent implements OnInit {
  private name = 'faq';
  pathToFile = `./assets/i18n/en/${this.name}.md`;

  constructor(private languageService: LanguageService,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.name = this.route.snapshot.paramMap.get('name');
    if (this.name) {
      this.languageService.currentLanguage.subscribe(
        data => {
          console.log(this.name);
          this.pathToFile = `./assets/i18n/${data}/${this.name}.md`;
        }
      );
    }
  }
}

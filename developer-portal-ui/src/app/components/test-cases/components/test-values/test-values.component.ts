import { Component } from '@angular/core';
import { LanguageService } from '../../../../services/language.service';
import { CustomizeService } from '../../../../services/customize.service';
import { CertificateService } from '../../../../services/certificate.service';
import { DataService } from '../../../../services/data.service';

@Component({
  selector: 'app-test-values',
  templateUrl: './test-values.component.html',
  styleUrls: ['./test-values.component.scss'],
})
export class TestValuesComponent {
  pathToTestValues = `./assets/content/i18n/en/test-cases/predefinedTestValues.md`;
  public certificate: string;
  constructor(
    private languageService: LanguageService,
    private customizeService: CustomizeService,
    private http: CertificateService,
    private dataService: DataService
  ) {}

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(data => {
      this.pathToTestValues = `${
        this.customizeService.currentLanguageFolder
      }/${data}/test-cases/predefinedTestValues.md`;
    });
    this.getJsonCertificate();
  }

  getJsonCertificate() {
    this.http
      .getJSON()
      .subscribe(data => (this.certificate = data.certificate));
  }

  save() {
    localStorage.setItem('certificate', JSON.stringify(this.certificate));
    if (localStorage.getItem('certificate')) {
      this.dataService.showToast('Certificate saved', 'Success!', 'success');
    }
  }

  clear() {
    localStorage.removeItem('certificate');
    this.dataService.showToast('Certificate delated', 'Success!', 'success');
  }
}

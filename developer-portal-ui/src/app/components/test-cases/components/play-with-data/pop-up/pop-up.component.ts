import { Component, Input, OnInit } from '@angular/core';
import { NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { CertificateService } from '../../../../../services/certificate.service';
import { DataService } from '../../../../../services/data.service';

@Component({
  selector: 'app-pop-up',
  templateUrl: './pop-up.component.html',
  styleUrls: ['./pop-up.component.scss'],
  providers: [NgbModalConfig, NgbModal],
})
export class PopUpComponent implements OnInit {
  public certificate;

  constructor(
    config: NgbModalConfig,
    private modalService: NgbModal,
    private http: CertificateService,
    private dataService: DataService
  ) {
    config.backdrop = 'static';
    config.keyboard = false;
  }

  open(content) {
    this.modalService.open(content);
  }
  getJsonCertificate() {
    this.http
      .getJSON()
      .subscribe(data => (this.certificate = data.certificate));
  }
  saveCertificate() {
    if (!this.certificate) {
      this.dataService.showToast(
        'Please, add certificate',
        'Warning!',
        'Warning'
      );
    } else {
      localStorage.setItem('certificate', JSON.stringify(this.certificate));
      this.dataService.showToast(
        'Certificate edited and saved',
        'Success!',
        'success'
      );
    }
  }
  clearCertificate() {
    localStorage.removeItem('certificate');
    this.certificate = '';
    this.dataService.showToast('Certificate deleted', 'Success!', 'success');
  }

  ngOnInit() {
    this.getJsonCertificate();
  }
}

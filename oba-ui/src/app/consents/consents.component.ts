import { Component, OnInit } from '@angular/core';
import { OnlineBankingConsentsService } from '../api/services';
import { AisAccountConsent } from '../api/models';
import { AuthService } from '../common/services/auth.service';

@Component({
  selector: 'app-consents',
  templateUrl: './consents.component.html',
  styleUrls: ['./consents.component.scss']
})
export class ConsentsComponent implements OnInit {

  consents: AisAccountConsent[] = [];

  constructor(
    private onlineBankingConsentsService: OnlineBankingConsentsService,
    private authService: AuthService) {}

  ngOnInit() {
    this.onlineBankingConsentsService.consentsUsingGETResponse(this.authService.getAuthorizedUser()).subscribe(consents => {
      this.consents = consents.body;
    });
  }

}

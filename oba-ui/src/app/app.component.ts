import {Component} from '@angular/core';
import {ShareDataService} from "./common/services/share-data.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  public operation: string;

  constructor(private sharedService: ShareDataService) {
    this.sharedService.currentOperation.subscribe(operation => {
      this.operation = operation;
    })
  }
}

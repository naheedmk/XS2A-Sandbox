import { Component } from '@angular/core';
import { CustomizeService, Theme } from '../../services/customize.service';
import { DataService } from '../../services/data.service';
import { LocalStorageService } from '../../services/local-storage.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-settings-modal',
  templateUrl: './settings-modal.component.html',
  styleUrls: ['./settings-modal.component.scss'],
})
export class SettingsModalComponent {
  importedTheme: Theme;

  constructor(
    private customizeService: CustomizeService,
    private dataService: DataService,
    private localStorageService: LocalStorageService
  ) {}

  save() {
    if (!!this.importedTheme) {
      this.customizeService.setUserTheme(this.importedTheme);
      this.localStorageService.set('userTheme', this.importedTheme);
      this.collapseThis();
    }
  }

  close() {
    this.customizeService.setDefaultTheme();
    this.collapseThis('modal');
  }

  onFileChanged(event) {
    const fileReader = new FileReader();
    fileReader.readAsText(event.target.files[0], 'UTF-8');
    fileReader.onload = () => {
      if (typeof fileReader.result === 'string') {
        try {
          this.importedTheme = JSON.parse(fileReader.result);
          const errors = this.validateTheme(this.importedTheme);
          if (errors.length !== 0) {
            this.dataService.showToast(
              JSON.stringify(errors),
              'Error!',
              'error'
            );
          } else {
            this.customizeService.setUserTheme(this.importedTheme);
          }
        } catch (e) {
          this.dataService.showToast('File is not valid!', 'Error!', 'error');
          console.log('invalid json file', e);
        }
      } else {
        this.dataService.showToast("It's not a JSON file", 'Error!', 'error');
      }
    };
    fileReader.onerror = error => {
      console.log(error);
    };
  }

  onFileClear(event) {
    event.target.value = null;
  }

  collapseThis(id?: string) {
    if (id === 'modal') {
      const collapsibleItemContent = document.getElementById(id);

      if (collapsibleItemContent.style.maxHeight) {
        collapsibleItemContent.style.maxHeight = '';
      } else {
        collapsibleItemContent.style.maxHeight = '20vw';
      }
    } else {
      const collapsibleItemContent = document.getElementById(id);

      if (collapsibleItemContent.style.maxHeight) {
        collapsibleItemContent.style.maxHeight = '';
      } else {
        collapsibleItemContent.style.maxHeight = '5vw';
      }
    }
  }

  exportTheme(type: string) {
    let tmp;
    if (this.localStorageService.get(type)) {
      tmp = this.localStorageService.get(type);
    } else {
      type = 'defaultTheme';
      tmp = this.localStorageService.get(type);
    }
    const blob = new Blob([JSON.stringify(tmp, null, 2)], {
      type: 'application/json',
    });
    saveAs(blob, type);
  }

  validateTheme(theme) {
    const general = ['globalSettings', 'contactInfo', 'officesInfo'];
    const additional = [
      ['logoPath'],
      ['imgPath', 'name', 'position'],
      ['city', 'company', 'addressFirstLine', 'addressSecondLine'],
    ];
    const errors: string[] = [];

    for (let i = 0; i < general.length; i++) {
      if (!theme.hasOwnProperty(general[i])) {
        errors.push(`Missing field ${general[i]}!`);
      } else if (i !== 2) {
        for (const property of additional[i]) {
          if (!theme[general[i]].hasOwnProperty(property)) {
            errors.push(`Field ${general[i]} missing property ${property}!`);
          }
        }
      } else {
        for (const office of theme.officesInfo) {
          for (const property of additional[i]) {
            if (!office.hasOwnProperty(property)) {
              errors.push(`Field ${general[i]} missing property ${property}!`);
            }
          }
        }
      }
    }

    return errors;
  }
}

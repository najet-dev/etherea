import { Component } from '@angular/core';

@Component({
  selector: 'app-brand',
  templateUrl: './brand.component.html',
  styleUrls: ['./brand.component.css'],
})
export class BrandComponent {
  sectionsState: { [key: string]: boolean } = {
    formules: false,
    tests: false,
    qualite: false,
  };

  toggleSection(section: string) {
    this.sectionsState[section] = !this.sectionsState[section];
  }
}

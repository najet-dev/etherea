import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
})
export class MenuComponent {
  isBurgerMenuOpen = false;

  toggleBurgerMenu() {
    this.isBurgerMenuOpen = !this.isBurgerMenuOpen;
  }
  constructor(private router: Router) {}

  isCurrentRoute(route: string): boolean {
    return this.router.url === route;
  }
}

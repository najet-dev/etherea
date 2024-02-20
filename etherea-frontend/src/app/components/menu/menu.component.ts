import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
})
export class MenuComponent {
  isBurgerMenuOpen = false;

  constructor(private router: Router) {
    // Écouter les événements de navigation pour fermer le menu
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.closeBurgerMenu();
      });
  }

  isCurrentRoute(route: string): boolean {
    return this.router.url === route;
  }

  toggleBurgerMenu() {
    this.isBurgerMenuOpen = !this.isBurgerMenuOpen;
  }

  closeBurgerMenu() {
    this.isBurgerMenuOpen = false;
  }
}

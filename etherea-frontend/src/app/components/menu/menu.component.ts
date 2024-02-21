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
  isBurgerIconVisible = true;

  constructor(private router: Router) {
    // Écouter les événements de navigation pour fermer le menu
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.closeBurgerMenu();
      });
  }

  isCurrentRoute(route: string): boolean {
    return this.router.url === route || this.router.url === '/'; // Ajout de la vérification pour la route '/'
  }

  toggleBurgerMenu() {
    this.isBurgerMenuOpen = !this.isBurgerMenuOpen;
    this.isBurgerIconVisible = !this.isBurgerIconVisible; // Inverser la visibilité de l'icône du menu burger
  }

  closeBurgerMenu() {
    this.isBurgerMenuOpen = false;
    this.isBurgerIconVisible = true; // Rétablir la visibilité de l'icône du menu burger
  }
}

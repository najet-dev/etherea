import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { StorageService } from 'src/app/services/storage.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
})
export class MenuComponent implements OnInit {
  isBurgerMenuOpen = false;
  isBurgerIconVisible = true;
  isLoggedIn: boolean = false;
  userId: number | null = null;

  constructor(
    private router: Router,
    private storageService: StorageService,
    private authService: AuthService
  ) {
    // Écouter les événements de navigation pour fermer le menu
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        console.log('Navigation event:', event); // Ajout d'un console.log pour vérifier les événements de navigation
        this.closeBurgerMenu();
      });
  }

  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
    this.storageService
      .isLoggedInObservable()
      .subscribe((loggedIn: boolean) => {
        console.log('Is logged in:', loggedIn); // Ajout d'un console.log pour vérifier l'état de connexion
        this.isLoggedIn = loggedIn;
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
  favorite(): void {
    if (!this.isLoggedIn) {
      this.router.navigate(['/signin']);
    } else {
      this.router.navigate(['/favorites']);
    }
  }

  logout() {
    console.log('Logging out'); // Ajout d'un message de log pour vérifier que la fonction est appelée

    // Appel à la méthode logout() du service AuthService
    this.authService.logout();
  }
}

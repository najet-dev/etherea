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

  constructor(
    private router: Router,
    private storageService: StorageService,
    private authService: AuthService
  ) {
    // Écouter les événements de navigation pour fermer le menu
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        console.log('Navigation event:', event); // Pour voir si la navigation se fait
      });
  }

  ngOnInit(): void {
    console.log('MenuComponent initialized'); // Confirmation de l'initialisation

    this.storageService.isLoggedInObservable().subscribe((isLoggedIn) => {
      console.log('Login status:', isLoggedIn); // Vérifiez l'état de connexion
      this.isLoggedIn = isLoggedIn; // Mettre à jour le statut de connexion
    });
  }

  isCurrentRoute(route: string): boolean {
    return this.router.url === route && route !== '/';
  }

  toggleBurgerMenu() {
    this.isBurgerMenuOpen = !this.isBurgerMenuOpen;
    this.isBurgerIconVisible = !this.isBurgerIconVisible; // Inverser la visibilité de l'icône du menu burger
  }

  closeBurgerMenu() {
    this.isBurgerMenuOpen = false;
    this.isBurgerIconVisible = true; // Rétablir la visibilité de l'icône du menu burger
  }
  logout() {
    console.log('Logging out'); // Confirmation que la méthode est appelée
    this.authService.logout(); // Appel à la méthode de déconnexion
    this.router.navigate(['/signin']); // Redirection vers la page de connexion
    console.log('Redirecting to /signin'); // Confirmez la redirection
  }
}

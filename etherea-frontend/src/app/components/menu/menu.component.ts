import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { FavoriteService } from 'src/app/services/favorite.service';
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
  favoriteCount: number = 0;

  constructor(
    private router: Router,
    private storageService: StorageService,
    private authService: AuthService,
    private favoriteService: FavoriteService
  ) {
    // Écouter les événements de navigation pour fermer le menu
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        console.log('Navigation event:', event);
        this.closeBurgerMenu();
      });
  }

  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
    this.storageService
      .isLoggedInObservable()
      .subscribe((loggedIn: boolean) => {
        console.log('Is logged in:', loggedIn);
        this.isLoggedIn = loggedIn;
      });
    this.favoriteService.favorites$.subscribe((favoriteIds: number[]) => {
      this.favoriteCount = favoriteIds.length; // Met à jour le nombre de favoris
    });

    // Récupérer l'utilisateur actuel et son ID
    this.authService.getCurrentUser().subscribe((user) => {
      if (user) {
        this.userId = user.id; // Extraire l'ID de l'utilisateur
        this.favoriteService.loadUserFavorites(this.userId); // Charger les favoris
      }
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
    console.log('Logging out');

    // Appel à la méthode logout() du service AuthService
    this.authService.logout().subscribe({
      next: () => {
        this.isLoggedIn = false; // Réinitialiser l'état de connexion
        this.favoriteCount = 0; // Réinitialiser le compteur de favoris
        console.log('User logged out successfully');
      },
      error: (err) => {
        console.error('Error during logout:', err);
      },
    });
  }
}

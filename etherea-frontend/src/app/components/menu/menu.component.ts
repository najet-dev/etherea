import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  Subject,
  switchMap,
} from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { CartService } from 'src/app/services/cart.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { StorageService } from 'src/app/services/storage.service';
import { Cart } from '../models/cart.model';
import { Product } from '../models';
import { AppFacade } from 'src/app/services/appFacade.service';

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
  cartCount: number = 0;
  searchQuery: string = '';
  filteredProducts: Product[] = [];
  private searchTerms = new Subject<string>();

  constructor(
    private router: Router,
    private storageService: StorageService,
    private authService: AuthService,
    private favoriteService: FavoriteService,
    private cartService: CartService,
    private appFacade: AppFacade
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
        this.isLoggedIn = loggedIn;

        if (this.isLoggedIn) {
          this.authService.getCurrentUser().subscribe((user) => {
            if (user) {
              this.userId = user.id;
              this.favoriteService.loadUserFavorites(this.userId); // Charger les favoris
              this.cartService.getCartItems(this.userId).subscribe(); // Charger les articles du panier
            }
          });
        }
      });
    this.favoriteService.favorites$.subscribe((favoriteIds: number[]) => {
      this.favoriteCount = favoriteIds.length;
    });

    this.cartService.carts$.subscribe((cartItems: Cart[]) => {
      this.cartCount = cartItems.reduce((acc, item) => acc + item.quantity, 0);
    });
    this.searchTerms
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((term: string) => this.appFacade.searchProductsByName(term))
      )
      .subscribe({
        next: (results) => {
          this.filteredProducts = results;
        },
        error: (err) => {
          console.error('Erreur recherche:', err);
          this.filteredProducts = [];
        },
      });
  }

  isCurrentRoute(route: string): boolean {
    return this.router.url === route || this.router.url === '/';
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

    this.authService.logout().subscribe({
      next: () => {
        this.isLoggedIn = false; // Réinitialiser l'état de connexion
        this.favoriteCount = 0; // Réinitialiser le compteur de favoris
        this.cartCount = 0;
        console.log('User logged out successfully');
      },
      error: (err) => {
        console.error('Error during logout:', err);
      },
    });
  }
  goToCart(): void {
    if (!this.isLoggedIn) {
      this.router.navigate(['/signin']);
    } else {
      this.router.navigate(['/cart']);
    }
  }

  onSearchChange(): void {
    if (this.searchQuery.trim().length > 1) {
      this.searchTerms.next(this.searchQuery.trim());
    } else {
      this.filteredProducts = [];
    }
  }

  goToProduct(product: Product): void {
    this.router.navigate(['/productDetails', product.id]);
    this.searchQuery = '';
    this.filteredProducts = [];
  }
}

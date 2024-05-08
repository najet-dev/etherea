import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { FavoriteService } from 'src/app/services/favorite.service';
import { Favorite } from '../models/favorite.model';
import { ProductService } from 'src/app/services/product.service';
import { IProduct } from '../models/i-product';
import { forkJoin, map, switchMap } from 'rxjs';

@Component({
  selector: 'app-favorite',
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.css'],
})
export class FavoriteComponent implements OnInit {
  favorites: Favorite[] = [];
  userId!: number;
  product: IProduct | null = null;

  constructor(
    private favoriteService: FavoriteService,
    private authService: AuthService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe((user) => {
      if (user && user.id) {
        this.userId = user.id;
        this.loadFavorites();
      }
    });
  }

  loadFavorites(): void {
    this.favoriteService
      .getUserFavorites(this.userId)
      .pipe(
        switchMap((favorites) => {
          const productObservables = favorites.map((favorite) =>
            this.productService.getProductById(favorite.productId)
          );
          return forkJoin(productObservables).pipe(
            map((products) => {
              favorites.forEach((favorite, index) => {
                favorite.product = products[index];
              });
              return favorites;
            })
          );
        })
      )
      .subscribe({
        next: (favorites) => {
          // Once all product details are loaded, assign the favorites
          this.favorites = favorites;
        },
        error: (error) => {
          console.error('Error loading favorites:', error);
        },
      });
  }

  removeFavorite(productId: number): void {
    this.favoriteService.removeFavorite(this.userId, productId).subscribe(
      (response) => {
        console.log(response);
        // Mettez à jour la liste des favoris après la suppression
        this.favorites = this.favorites.filter(
          (favorite) => favorite.productId !== productId
        );
      },
      (error) => {
        console.error('Error removing favorite:', error);
      }
    );
  }
}

import { EventEmitter, Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { Cart } from '../components/models/cart.model';
import { Favorite } from '../components/models/favorite.model';
import { IProduct, ProductType } from '../components/models/i-product';
import { CartService } from './cart.service';
import { FavoriteService } from './favorite.service';
import { ProductService } from './product.service';

@Injectable({
  providedIn: 'root',
})
export class AppFacade {
  constructor(
    public cartService: CartService,
    public favoriteService: FavoriteService,
    private productService: ProductService
  ) {}

  // Cart
  getCartItems(userId: number): Observable<Cart[]> {
    return this.cartService.getCartItems(userId);
  }

  addToCart(cart: Cart): Observable<Cart> {
    return this.cartService.addToCart(cart).pipe(
      catchError((error) => {
        console.error('Error adding to cart:', error);
        return throwError(() => new Error('Failed to add item to cart.'));
      })
    );
  }

  updateCartItem(
    userId: number,
    productId: number,
    newQuantity: number
  ): Observable<Cart> {
    return this.cartService.updateCartItem(userId, productId, newQuantity).pipe(
      catchError((error) => {
        console.error('Error updating cart item:', error);
        return throwError(() => new Error('Failed to update cart item.'));
      })
    );
  }

  deleteCartItem(id: number): Observable<void> {
    return this.cartService.deleteCartItem(id).pipe(
      catchError((error) => {
        console.error('Error deleting cart item:', error);
        return throwError(() => new Error('Failed to delete cart item.'));
      })
    );
  }

  // Favorite
  getUserFavorites(userId: number): Observable<Favorite[]> {
    return this.favoriteService.getUserFavorites(userId);
  }

  addFavorite(userId: number, productId: number): Observable<Favorite> {
    return this.favoriteService.addFavorite(userId, productId);
  }

  removeFavorite(userId: number, productId: number): Observable<void> {
    return this.favoriteService.removeFavorite(userId, productId);
  }

  toggleFavorite(product: IProduct): void {
    this.favoriteService.toggleFavorite(product);
  }

  productsFavorites(products: IProduct[]): Observable<IProduct[]> {
    return this.favoriteService.productsFavorites(products);
  }

  // Products
  getProducts(limit?: number): Observable<IProduct[]> {
    return this.productService.getProducts(limit);
  }

  getProductsByType(
    type: ProductType,
    page: number,
    size: number
  ): Observable<IProduct[]> {
    return this.productService.getProductsByType(type, page, size);
  }

  getProductById(id: number): Observable<IProduct> {
    return this.productService.getProductById(id);
  }
}

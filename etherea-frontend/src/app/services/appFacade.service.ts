import { EventEmitter, Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { FavoriteService } from './favorite.service';
import { ProductService } from './product.service';
import { CartService } from './cart.service';
import { SigninRequest } from '../components/models/signinRequest.model';
import { SignupRequest } from '../components/models/SignupRequest.model';
import { Cart } from '../components/models/cart.model';
import { Favorite } from '../components/models/favorite.model';
import { IProduct } from '../components/models/i-product.model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { IProductVolume } from '../components/models/IProductVolume.model';

@Injectable({
  providedIn: 'root',
})
export class AppFacade {
  constructor(
    public cartService: CartService,
    public favoriteService: FavoriteService,
    private productService: ProductService
  ) {}

  // cart
  getCartItems(userId: number): Observable<Cart[]> {
    return this.cartService.getCartItems(userId);
  }

  addToCart(cart: Cart): Observable<Cart> {
    return this.cartService.addToCart(cart);
  }

  updateCartItem(
    userId: number,
    productId: number,
    newQuantity: number
  ): Observable<Cart> {
    return this.cartService.updateCartItem(userId, productId, newQuantity);
  }
  deleteCartItem(id: number): Observable<void> {
    return this.cartService.deleteCartItem(id);
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
    type: string,
    page: number,
    size: number
  ): Observable<IProduct[]> {
    return this.productService.getProductsByType(type, page, size);
  }

  getProductById(id: number): Observable<IProduct> {
    return this.productService.getProductById(id);
  }
}

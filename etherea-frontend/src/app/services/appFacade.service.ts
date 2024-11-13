import { EventEmitter, Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { FavoriteService } from './favorite.service';
import { ProductService } from './product.service';
import { CartService } from './cart.service';
import { Cart } from '../components/models/cart.model';
import { Favorite } from '../components/models/favorite.model';
import { Product } from '../components/models/Product.model';
import { DeliveryAddress } from '../components/models/DeliveryAddress.model';
import { OrderService } from './order.service';
import { SignupRequest } from '../components/models/SignupRequest.model';
import { UserService } from './user.service';
import { DeliveryAddressService } from './delivery-address.service';

@Injectable({
  providedIn: 'root',
})
export class AppFacade {
  constructor(
    public cartService: CartService,
    public favoriteService: FavoriteService,
    private productService: ProductService,
    private deliveryAddressService: DeliveryAddressService,
    private userService: UserService
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
    newQuantity: number,
    volumeId?: number
  ): Observable<Cart> {
    return this.cartService.updateCartItem(
      userId,
      productId,
      newQuantity,
      volumeId
    );
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

  toggleFavorite(product: Product): void {
    this.favoriteService.toggleFavorite(product);
  }

  productsFavorites(products: Product[]): Observable<Product[]> {
    return this.favoriteService.productsFavorites(products);
  }

  // Products
  getProducts(limit?: number): Observable<Product[]> {
    return this.productService.getProducts(limit);
  }

  getProductsByType(
    type: string,
    page: number,
    size: number
  ): Observable<Product[]> {
    return this.productService.getProductsByType(type, page, size);
  }

  getProductById(id: number): Observable<Product> {
    return this.productService.getProductById(id);
  }

  // DeliveryAddress
  getUserDeliveryAddresses(userId: number): Observable<DeliveryAddress[]> {
    return this.deliveryAddressService.getUserDeliveryAddresses(userId);
  }

  getDeliveryAddress(
    userId: number,
    addressId: number
  ): Observable<DeliveryAddress> {
    return this.deliveryAddressService.getDeliveryAddress(userId, addressId);
  }

  addDeliveryAddress(
    userId: number,
    deliveryAddress: DeliveryAddress
  ): Observable<DeliveryAddress> {
    return this.deliveryAddressService.addDeliveryAddress(
      userId,
      deliveryAddress
    );
  }

  updateDeliveryAddress(
    userId: number,
    deliveryAddress: DeliveryAddress
  ): Observable<DeliveryAddress> {
    return this.deliveryAddressService.updateDeliveryAddress(
      userId,
      deliveryAddress
    );
  }

  // User
  getUserDetails(userId: number): Observable<SignupRequest | null> {
    return this.userService.getUserDetails(userId);
  }

  getCurrentUserId(): Observable<number | null> {
    return this.userService.getCurrentUserId();
  }
}

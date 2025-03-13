import { EventEmitter, Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { FavoriteService } from './favorite.service';
import { ProductService } from './product.service';
import { CartService } from './cart.service';
import { Cart } from '../components/models/cart.model';
import { Favorite } from '../components/models/favorite.model';
import { Product } from '../components/models/Product.model';
import { DeliveryAddress } from '../components/models/DeliveryAddress.model';
import { SignupRequest } from '../components/models/SignupRequest.model';
import { UserService } from './user.service';
import { DeliveryAddressService } from './delivery-address.service';
import { DeliveryMethodService } from './delivery-method.service';
import { PaymentService } from './payment.service';
import { PaymentRequest } from '../components/models/PaymentRequest.model';
import { PaymentResponse } from '../components/models/PaymentResponse.model';
import { DeliveryMethod } from '../components/models/DeliveryMethod.model';
import { DeliveryType } from '../components/models/DeliveryType.model';
import { UpdateDeliveryMethodRequest } from '../components/models/UpdateDeliveryMethodRequest.model';
import { CookieConsentService } from './cookie-consent.service';
import { CookieConsent } from '../components/models/CookieConsent.model';
import { CookieChoice } from '../components/models/cookie-choice.model';

@Injectable({
  providedIn: 'root',
})
export class AppFacade {
  constructor(
    public cartService: CartService,
    public favoriteService: FavoriteService,
    private productService: ProductService,
    public deliveryAddressService: DeliveryAddressService,
    private userService: UserService,
    public deliveryMethodService: DeliveryMethodService,
    public paymentService: PaymentService,
    private cookieConsentService: CookieConsentService
  ) {}

  // cartItem
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
  getCurrentUserDetails(): Observable<SignupRequest | null> {
    return this.userService.getCurrentUserDetails();
  }

  getUserDetails(userId: number): Observable<SignupRequest | null> {
    return this.userService.getUserDetails(userId);
  }

  //Method
  getDeliveryTypes(userId: number): Observable<DeliveryType[]> {
    return this.deliveryMethodService.getDeliveryTypes(userId);
  }
  getUserDeliveryMethods(userId: number): Observable<DeliveryMethod[]> {
    return this.deliveryMethodService.getUserDeliveryMethods(userId);
  }
  updateDeliveryMethod(
    deliveryMethodId: number,
    request: UpdateDeliveryMethodRequest
  ) {
    return this.deliveryMethodService.updateDeliveryMethod(
      deliveryMethodId,
      request
    );
  }
  //cart
  getCartId(userId: number): Observable<number> {
    return this.cartService.getCartId(userId);
  }
  //payment
  createPayment(paymentRequest: PaymentRequest): Observable<PaymentResponse> {
    return this.paymentService.createPayment(paymentRequest);
  }
  confirmPayment(
    paymentIntentId: string,
    paymentMethodId: string
  ): Observable<PaymentResponse> {
    return this.paymentService.confirmPayment(paymentIntentId, paymentMethodId);
  }
  //cookie-consent
  acceptAllCookies(sessionId: string): Observable<CookieConsent | null> {
    return this.cookieConsentService.acceptAllCookies(sessionId);
  }
  rejectAllCookies(sessionId: string): Observable<CookieConsent | null> {
    return this.cookieConsentService.rejectAllCookies(sessionId);
  }
  customizeCookies(
    sessionId: string,
    cookieChoices: CookieChoice[]
  ): Observable<CookieConsent | null> {
    return this.cookieConsentService.customizeCookies(sessionId, cookieChoices);
  }
  getCookiesList(): Observable<CookieChoice[]> {
    return this.cookieConsentService.getCookiesList();
  }
}

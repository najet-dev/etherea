import { EventEmitter, Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { FavoriteService } from './favorite.service';
import { ProductService } from './product.service';
import { CartService } from './cart.service';
import { Cart } from '../components/models/cart.model';
import { Favorite } from '../components/models/favorite.model';
import { Product } from '../components/models/product.model';
import { DeliveryAddress } from '../components/models/deliveryAddress.model';
import { SignupRequest } from '../components/models/SignupRequest.model';
import { UserService } from './user.service';
import { DeliveryAddressService } from './delivery-address.service';
import { DeliveryMethodService } from './delivery-method.service';
import { PaymentService } from './payment.service';
import { PaymentRequest } from '../components/models/paymentRequest.model';
import { PaymentResponse } from '../components/models/paymentResponse.model';
import { DeliveryMethod } from '../components/models/deliveryMethod.model';
import { DeliveryType } from '../components/models/deliveryType.model';
import { UpdateDeliveryMethodRequest } from '../components/models/updateDeliveryMethodRequest.model';
import { CookieConsentService } from './cookie-consent.service';
import { CookieConsent } from '../components/models/cookieConsent.model';
import { CookieChoice } from '../components/models/cookie-choice.model';
import { UpdateEmailRequest } from '../components/models/updateEmailRequest.model';
import { UpdatePasswordRequest } from '../components/models/updatePasswordRequest.model';
import { ForgotPasswordResponse } from '../components/models/forgotPasswordResponse.model';
import { ForgotPasswordRequest } from '../components/models/forgotPasswordRequest.model';
import { PasswordResetService } from './password-reset.service';
import { ResetPasswordRequest } from '../components/models/resetPasswordRequest.model';
import { ResetPasswordResponse } from '../components/models/resetPasswordResponse.model';
import { Newsletter } from '../components/models/newsletter.model';

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
    private cookieConsentService: CookieConsentService,
    private passwordResetService: PasswordResetService
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

  updateEmail(updateEmailRequest: UpdateEmailRequest): Observable<string> {
    return this.userService.updateEmail(updateEmailRequest);
  }
  updatePassword(
    updatePasswordRequest: UpdatePasswordRequest
  ): Observable<string> {
    return this.userService.updatePassword(updatePasswordRequest);
  }
  subscribeToNewsletter(
    newsletter: Newsletter
  ): Observable<{ message: string }> {
    return this.userService.subscribeToNewsletter(newsletter);
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
  getSessionId(): Observable<string> {
    return this.cookieConsentService.getSessionId();
  }
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
  //password
  sendResetLink(
    request: ForgotPasswordRequest
  ): Observable<ForgotPasswordResponse> {
    return this.passwordResetService.sendResetLink(request);
  }
  resetPassword(
    request: ResetPasswordRequest
  ): Observable<ResetPasswordResponse> {
    return this.passwordResetService.resetPassword(request);
  }
}

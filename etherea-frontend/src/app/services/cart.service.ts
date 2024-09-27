import { EventEmitter, Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Cart } from '../components/models/cart.model';
import { IProduct, ProductType } from '../components/models/i-product';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = environment.apiUrl;
  cartUpdated: EventEmitter<void> = new EventEmitter<void>();

  constructor(private httpClient: HttpClient) {}

  // Retrieve cart items for a user
  getCartItems(userId: number): Observable<Cart[]> {
    return this.httpClient.get<Cart[]>(`${this.apiUrl}/cart/${userId}`).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          return throwError(() => new Error('User or cart not found.'));
        }
        return throwError(() => new Error('Failed to load cart items.'));
      })
    );
  }

  // Add an item to the cart
  addToCart(cart: Cart): Observable<Cart> {
    console.log('Sending cart data:', cart);

    // Check for required data
    if (!cart.productId || !cart.userId || cart.quantity <= 0) {
      console.error(
        'Missing data or invalid quantity for adding to cart:',
        cart
      );
      return throwError(
        () => new Error('Product, user not defined, or invalid quantity.')
      );
    }

    let params = new HttpParams()
      .set('userId', cart.userId.toString())
      .set('productId', cart.productId.toString())
      .set('quantity', cart.quantity.toString());

    // Check for HAIR products
    if (cart.product.type === ProductType.HAIR) {
      if (cart.selectedVolume) {
        console.log('Selected volume when adding:', cart.selectedVolume);
        params = params.append('volumeId', cart.selectedVolume.id.toString());
      } else {
        return throwError(
          () => new Error('Volume ID is required for HAIR products.')
        );
      }
    }

    // Check for FACE products
    if (cart.product.type === ProductType.FACE && cart.selectedVolume) {
      return throwError(
        () => new Error('FACE products should not have a volume.')
      );
    }

    return this.httpClient
      .post<Cart>(`${this.apiUrl}/cart/addToCart`, cart, { params }) // Pass cart directly
      .pipe(
        tap(() => this.cartUpdated.emit()),
        catchError((error) => {
          console.error('Error adding item to cart:', error);
          return throwError(() => new Error('Failed to add item to cart.'));
        })
      );
  }

  // Update the quantity of a cart item
  updateCartItem(
    userId: number,
    productId: number,
    newQuantity: number,
    volumeId?: number
  ): Observable<any> {
    if (newQuantity <= 0) {
      return throwError(() => new Error('Quantity must be greater than 0.'));
    }

    // Construct the URL based on product type
    let url: string;
    if (volumeId) {
      // URL for HAIR products
      url = `${this.apiUrl}/cart/${userId}/products/${productId}/volume/${volumeId}`;
    } else {
      // URL for FACE products
      url = `${this.apiUrl}/cart/${userId}/products/${productId}`;
    }

    // Log the generated URL
    console.log('Generated URL for PUT request:', url);

    const params = new HttpParams().set('quantity', newQuantity.toString());

    // Perform the PUT request
    return this.httpClient.put<any>(url, null, { params }).pipe(
      tap(() => console.log('Cart item updated successfully')),
      catchError((error) => {
        console.error('Error updating cart item:', error);
        return throwError(() => new Error('Failed to update cart item.'));
      })
    );
  }

  // Remove an item from the cart
  deleteCartItem(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/cart/${id}`).pipe(
      tap(() => this.cartUpdated.emit()), // Emit update after deletion
      catchError((error) => {
        console.error('Error deleting cart item:', error);
        return throwError(() => new Error('Failed to delete cart item.'));
      })
    );
  }
}

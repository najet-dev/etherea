import { Observable } from 'rxjs';
import { Cart } from './cart.model';

export interface CartFacadeInterface {
  getCartItems(): Observable<Cart[]>;
  addToCart(cart: Cart): Observable<Cart>;
  updateCartItem(
    userId: number,
    productId: number,
    newQuantity: number
  ): Observable<Cart>;
  deleteCartItem(id: number): Observable<Cart>;
}

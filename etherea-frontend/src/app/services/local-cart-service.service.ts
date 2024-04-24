import { Injectable } from '@angular/core';
import { Cart } from '../components/models/cart.model';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class LocalCartService {
  private cartKey = 'cartItems';

  constructor(private storageService: StorageService) {}

  getCartItemsFromLocal(): Cart[] {
    return this.storageService.get('cart') || [];
  }

  addToCartToLocal(cart: Cart): void {
    const localCart = this.storageService.get('cart') || [];
    localCart.push(cart);
    this.storageService.set('cart', localCart);
  }
}

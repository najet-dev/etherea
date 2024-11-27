import { Injectable } from '@angular/core';
import { Cart } from '../components/models/cart.model';
import { ProductTypeService } from './product-type.service';

@Injectable({
  providedIn: 'root',
})
export class CartCalculationService {
  constructor(private productTypeService: ProductTypeService) {}

  /**
   * Calcule le total du panier.
   * @param cartItems Liste des articles du panier.
   * @returns Le montant total du panier.
   */
  calculateCartTotal(cartItems: Cart[]): number {
    return cartItems.reduce((total, item) => {
      if (item.product) {
        if (
          this.productTypeService.isHairProduct(item.product) &&
          item.selectedVolume
        ) {
          item.subTotal = item.selectedVolume.price * item.quantity;
        } else if (
          this.productTypeService.isFaceProduct(item.product) &&
          item.product.basePrice !== undefined
        ) {
          item.subTotal = item.product.basePrice * item.quantity;
        }

        return total + (item.subTotal || 0);
      }
      return total;
    }, 0);
  }
}

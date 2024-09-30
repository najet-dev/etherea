import { Cart } from './cart.model';
import { Product } from './Product.model';
import { ProductVolume } from './ProductVolume.model';

export interface Favorite {
  id: number;
  userId: number;
  productId: number;
  product: Product;
}

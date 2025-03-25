import { Product } from './Product.model';

export interface Favorite {
  id: number;
  userId: number;
  productId: number;
  product: Product;
}

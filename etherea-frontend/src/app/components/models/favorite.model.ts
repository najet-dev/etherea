import { Product } from './product.model';

export interface Favorite {
  id: number;
  userId: number;
  productId: number;
  product: Product;
}

import { Product } from './product.model';

export interface Favorite {
  id: number;
  user: number;
  productId: number;
  product: Product;
}

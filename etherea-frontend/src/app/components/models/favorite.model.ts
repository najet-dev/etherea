import { Cart } from './cart.model';
import { IProduct } from './i-product.model';
import { IProductVolume } from './IProductVolume.model';

export interface Favorite {
  id: number;
  userId: number;
  productId: number;
  product: IProduct;
}

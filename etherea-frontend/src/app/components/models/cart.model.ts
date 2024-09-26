import { IProduct } from './i-product';
import { Volume } from './volume.model';

export interface Cart {
  id: number;
  userId: number;
  product: IProduct;
  productId: number;
  quantity: number;
  selectedVolume: Volume | null;
  subTotal?: number;
  total?: number;
}

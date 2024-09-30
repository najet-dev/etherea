import { IProduct } from './i-product';
import { Volume } from './Volume.model';

export interface Cart {
  id: number;
  userId: number;
  product: IProduct;
  productId: number;
  quantity: number;
  selectedVolume?: Volume;
  volumeId?: number;
  subTotal?: number;
  total?: number;
}

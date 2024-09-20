import { IProduct } from './i-product';
import { Volume } from './volume.model';

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

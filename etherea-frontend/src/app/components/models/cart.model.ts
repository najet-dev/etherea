import { IProduct } from './i-product.model';
import { IProductVolume } from './IProductVolume.model';

export interface Cart {
  id: number;
  userId: number;
  product: IProduct;
  productId: number;
  quantity: number;
  subTotal?: number;
  total?: number;
  selectedVolume?: IProductVolume;
  volume?: { id: number; volume: number; price: number } | null;
}

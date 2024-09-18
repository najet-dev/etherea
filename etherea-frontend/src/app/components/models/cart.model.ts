import { IProduct } from './i-product';
import { Volume } from './volume.model';

export interface Cart {
  id: number;
  userId: number;
  product?: IProduct;
  productId: number;
  quantity: number;
  selectedVolume?: Volume;
  subTotal?: number;
  total?: number;
  volume?: Volume; // Propriété qui vient de l'API, renvoyée pour chaque article
}

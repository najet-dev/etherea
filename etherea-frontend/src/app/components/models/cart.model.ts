import { FaceProduct } from './faceProduct.model';
import { HairProduct } from './hairProduct.model';
import { Product } from './Product.model';
import { ProductVolume } from './productVolume.model';

export interface Cart {
  id: number;
  userId: number;
  product: Product;
  hairProduct: HairProduct | null;
  faceProduct: FaceProduct | null;
  productId: number;
  quantity: number;
  //volumeId?: number;
  subTotal?: number;
  total?: number;
  selectedVolume?: ProductVolume;
  volume?: { id: number; volume: number; price: number } | null;
}

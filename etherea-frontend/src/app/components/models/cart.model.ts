import { Product } from './Product.model';
import { FaceProduct } from './FaceProduct.model';
import { HairProduct } from './HairProduct.model';
import { ProductVolume } from './ProductVolume.model';

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

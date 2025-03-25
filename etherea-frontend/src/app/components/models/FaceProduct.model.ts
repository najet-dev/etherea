import { Product } from './product.model';
import { ProductType } from './productType.enum';

export interface FaceProduct extends Product {
  type: ProductType.FACE;
  basePrice: number;
}

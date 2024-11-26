import { Product } from './Product.model';
import { ProductType } from './ProductType.enum';

export interface FaceProduct extends Product {
  type: ProductType.FACE;
  basePrice: number;
}

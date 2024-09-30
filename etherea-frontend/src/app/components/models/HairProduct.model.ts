import { Product } from './Product.model';
import { ProductType } from './ProductType.enum';
import { ProductVolume } from './ProductVolume.model';

export interface HairProduct extends Product {
  type: ProductType.HAIR;
  volumes: ProductVolume[];
}

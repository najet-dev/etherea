import { Product } from './product.model';
import { ProductType } from './productType.enum';
import { ProductVolume } from './productVolume.model';

export interface HairProduct extends Product {
  type: ProductType.HAIR;
  volumes: ProductVolume[];
}

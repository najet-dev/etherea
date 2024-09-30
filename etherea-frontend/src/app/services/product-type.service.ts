import { Injectable } from '@angular/core';
import { Product } from '../components/models/Product.model';
import { HairProduct } from '../components/models/HairProduct.model';
import { ProductType } from '../components/models/ProductType.enum';
import { FaceProduct } from '../components/models/FaceProduct.model';

@Injectable({
  providedIn: 'root',
})
export class ProductTypeService {
  constructor() {}

  isHairProduct(product: Product): product is HairProduct {
    return product?.type === ProductType.HAIR;
  }

  isFaceProduct(product: Product): product is FaceProduct {
    return product?.type === ProductType.FACE;
  }
}

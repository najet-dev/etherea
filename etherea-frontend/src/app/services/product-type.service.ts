import { Injectable } from '@angular/core';
import { Product } from '../components/models/product.model';
import { HairProduct } from '../components/models/hairProduct.model';
import { ProductType } from '../components/models/productType.enum';
import { FaceProduct } from '../components/models/faceProduct.model';

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

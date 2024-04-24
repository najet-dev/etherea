import { IProduct } from './i-product';

export interface Cart {
  id: number;
  userId: number;
  product: IProduct;
  productId: number;
  quantity: number;
  subTotal?: number;
  total?: number;
}

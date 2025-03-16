import { ProductType } from './productType.enum';

export interface Product {
  id: number;
  name: string;
  description: string;
  type: ProductType;
  stockQuantity: number;
  stockStatus: string;
  benefits: string;
  usageTips: string;
  ingredients: string;
  characteristics: string;
  image: string;
  isFavorite?: boolean;
}

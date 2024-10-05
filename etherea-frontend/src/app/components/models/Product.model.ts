import { ProductType } from './ProductType.enum';

export interface Product {
  id: number;
  name: string;
  description: string;
  type: ProductType;
  stockStatus: string;
  benefits: string;
  usageTips: string;
  ingredients: string;
  characteristics: string;
  image: string;
  isFavorite?: boolean;
}

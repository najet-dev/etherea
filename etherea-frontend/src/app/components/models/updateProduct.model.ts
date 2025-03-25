import { ProductType } from './productType.enum';
import { StockStatus } from './stock-status.enum';

export interface UpdateProduct {
  id: number;
  name: string;
  description: string;
  type: ProductType;
  benefits: string;
  usageTips: string;
  ingredients: string;
  basePrice: number;
  characteristics: string;
  stockQuantity: number;
  stockStatus: StockStatus;
  image: string;
}

import { ProductVolume } from './productVolume.model';

export interface IProduct {
  id: number;
  name: string;
  description: string;
  price: number;
  type: string;
  stockStatus: string;
  benefits: string;
  usageTips: string;
  ingredients: string;
  characteristics: string;
  image: string;
  isFavorite?: boolean;
  volumes?: ProductVolume[];
}

export enum ProductType {
  FACE = 'FACE',
  HAIR = 'HAIR',
}

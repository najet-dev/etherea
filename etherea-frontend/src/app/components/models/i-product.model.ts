import { IProductVolume } from './IProductVolume.model';

export interface IProduct {
  id: number;
  name: string;
  description: string;
  type: string;
  basePrice: number;
  stockStatus: string;
  benefits: string;
  usageTips: string;
  ingredients: string;
  characteristics: string;
  image: string;
  volumes: IProductVolume[];
  isFavorite?: boolean;
}

export enum ProductType {
  FACE = 'FACE',
  HAIR = 'HAIR',
}

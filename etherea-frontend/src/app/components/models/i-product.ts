import { Volume } from './volume.model';

export interface IProduct {
  id: number;
  name: string;
  description: string;
  type: string;
  stockStatus: string;
  benefits: string;
  usageTips: string;
  ingredients: string;
  characteristics: string;
  image: string;
  isFavorite?: boolean;
  volumes?: Volume[];
}

export enum ProductType {
  FACE = 'FACE',
  HAIR = 'HAIR',
}

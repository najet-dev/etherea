import { IProductVolume } from './IProductVolume.model';

export interface IProduct {
  id: number;
  name: string;
  description: string;
  type: string;
  stockStatus: string;
  benefits: String;
  usageTips: String;
  ingredients: String;
  characteristics: String;
  image: string;
  volumes: IProductVolume[];
  isFavorite?: boolean;
}

export enum ProductType {
  FACE = 'FACE',
  HAIR = 'HAIR',
}

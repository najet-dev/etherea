import { Volume } from './volume.model';
export interface IProduct {
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
  volumes?: Volume[]; // Présent uniquement si type === HAIR
  basePrice?: number; // Ajouté pour les produits de type FACE
}

export enum ProductType {
  FACE = 'FACE',
  HAIR = 'HAIR',
}

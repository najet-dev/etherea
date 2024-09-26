import { Volume } from './volume.model';
export interface IProduct {
  id: number;
  name: string;
  description: string;
  type: ProductType;
  basePrice?: number; // Ajouté pour les produits de type FACE
  stockStatus: string;
  benefits: string;
  usageTips: string;
  ingredients: string;
  characteristics: string;
  image: string;
  isFavorite?: boolean;
  volumes?: Volume[]; // Présent uniquement si type === HAIR
}

export enum ProductType {
  FACE = 'FACE',
  HAIR = 'HAIR',
}

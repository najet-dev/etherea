export interface IProduct {
  id: number;
  name: string;
  description: string;
  price: number;
  type: string;
  stockStatus: string;
  benefits: String;
  usageTips: String;
  ingredients: String;
  characteristics: String;
  image: string;
  isFavorite?: boolean;
}

export enum ProductType {
  FACE = 'FACE',
  HAIR = 'HAIR',
}

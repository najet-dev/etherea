export interface IProduct {
  id: number;
  name: string;
  description: string;
  price: number;
  type: string;
  stockAvailable: number;
  benefits: String;
  usageTips: String;
  ingredients: String;
  characteristics: String;
  image: string;
}
export enum ProductType {
  FACE = 'FACE',
  HAIR = 'HAIR',
}

export interface IProduct {
  id: number;
  name: string;
  description: string;
  quantity: number;
  price: number;
  stockAvailable: number;
  benefits: String;
  usageTips: String;
  ingredients: String;
  characteristics: String;
  image: string;
  imageBlob?: Blob;
}

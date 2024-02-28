export interface IProduct {
  id: number;
  name: string;
  description: string;
  price: number;
  stockAvailable: number;
  image: string;
  imageBlob?: Blob;
}

import { IProduct } from './i-product';
import { Volume } from './volume.model';

export interface Cart {
  id: number;
  userId: number;
  product: IProduct;
  productId: number;
  quantity: number;
  selectedVolume?: Volume | null; // Ajoutez ce champ si ce n'est pas déjà fait
  subTotal?: number;
  total?: number;
}

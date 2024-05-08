import { IProduct } from './i-product';

export interface Favorite {
  id: number;
  userId: number; // Identifiant de l'utilisateur qui a ajouté le favori
  productId: number; // Identifiant du produit favori
  product: IProduct;
}

import { Component, Inject, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogConfig,
  MatDialogRef,
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { IProduct } from '../models/i-product';
import { Cart } from '../models/cart.model';

@Component({
  selector: 'app-product-summary-modal',
  templateUrl: './product-summary.component.html',
  styleUrls: ['./product-summary.component.css'],
})
export class ProductSummaryComponent implements OnInit {
  constructor(
    private dialogRef: MatDialogRef<ProductSummaryComponent>, // Référence à la boîte de dialogue actuelle
    @Inject(MAT_DIALOG_DATA) // Injection des données passées à la boîte de dialogue
    public data: {
      product: IProduct;
      cart: Cart;
      quantity: number;
      subTotal: number;
    },
    private router: Router
  ) {}

  ngOnInit(): void {
    // Définition de la configuration de la boîte de dialogue
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '60%'; // 60% de la largeur de la fenêtre
    dialogConfig.height = '80%'; // 80% de la hauteur de la fenêtre
    this.dialogRef.updateSize(dialogConfig.width, dialogConfig.height); // Mise à jour de la taille de la boîte de dialogue
    console.log('Product Data:', this.data.product);
    console.log('Cart Data:', this.data.cart);
    console.log('Selected Volume:', this.data.cart.selectedVolume);
  }

  // Fonction pour continuer les achats (ferme la boîte de dialogue)
  continueShopping(): void {
    this.dialogRef.close();
  }

  // Fonction pour aller au panier
  goToCart(): void {
    this.dialogRef.close(); // Ferme la boîte de dialogue
    this.router.navigateByUrl('/cart'); // Navigue vers la page du panier
  }

  // Fonction pour aller à la page d'accueil
  goToShopping(productId: number): void {
    this.dialogRef.close(); // Ferme la boîte de dialogue
    this.router.navigateByUrl('/'); // Navigue vers la page d'accueil
  }
}

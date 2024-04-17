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
    private dialogRef: MatDialogRef<ProductSummaryComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      product: IProduct;
      cart: Cart;
      quantity: number;
      subTotal: number;
    },
    private router: Router
  ) {}

  ngOnInit(): void {
    // Définir la configuration de la modal
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '60%'; // 50% de la largeur de la fenêtre
    dialogConfig.height = '80%'; // 70% de la hauteur de la fenêtre
    this.dialogRef.updateSize(dialogConfig.width, dialogConfig.height);
  }
  continueShopping(): void {
    this.dialogRef.close();
  }

  goToCart(): void {
    this.dialogRef.close();
    this.router.navigateByUrl('/cart');
  }
  goToShopping(productId: number): void {
    this.dialogRef.close();
    this.router.navigateByUrl('/productDetails/' + productId);
  }
}

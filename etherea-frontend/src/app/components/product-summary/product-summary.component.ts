import { Component, Inject, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogConfig,
  MatDialogRef,
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { IProduct, ProductType } from '../models/i-product.model';
import { Cart } from '../models/cart.model';

@Component({
  selector: 'app-product-summary-modal',
  templateUrl: './product-summary.component.html',
  styleUrls: ['./product-summary.component.css'],
})
export class ProductSummaryComponent implements OnInit {
  ProductType = ProductType;

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
    this.updateDialogSize();

    if (!this.data.product) {
      console.error('Produit non défini dans les données du dialogue.');
      return;
    }

    console.log('Product Data:', this.data.product);
    console.log('Cart Data:', this.data.cart);
    console.log('Selected Volume:', this.data.cart.selectedVolume);
  }

  private updateDialogSize(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '60%';
    dialogConfig.height = '80%';
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
    this.router.navigateByUrl('/');
  }
}

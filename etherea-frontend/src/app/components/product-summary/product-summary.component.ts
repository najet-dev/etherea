import { Component, Inject, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogConfig,
  MatDialogRef,
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { IProduct, ProductType } from '../models/i-product.model';
import { Cart } from '../models/cart.model';
import { IProductVolume } from '../models/IProductVolume.model';

@Component({
  selector: 'app-product-summary-modal',
  templateUrl: './product-summary.component.html',
  styleUrls: ['./product-summary.component.css'],
})
export class ProductSummaryComponent implements OnInit {
  product!: IProduct;
  quantity!: number;
  selectedVolume!: IProductVolume | null;
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

    this.product = this.data.product;
    this.quantity = this.data.quantity;

    // Add a check for this.data.cart before accessing selectedVolume
    if (this.data.cart) {
      this.selectedVolume = this.data.cart.selectedVolume || null;
      console.log('Cart Data:', this.data.cart);
      console.log('Selected Volume:', this.selectedVolume);
    } else {
      this.selectedVolume = null; // Default to null if cart is undefined
      console.warn('Cart non défini dans les données du dialogue.');
    }

    console.log('Product Data:', this.data.product);
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

import { Component, Inject, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogConfig,
  MatDialogRef,
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { IProduct, ProductType } from '../models/i-product';
import { Cart } from '../models/cart.model';
import { Volume } from '../models/volume.model'; // Assurez-vous que Volume est bien importé

@Component({
  selector: 'app-product-summary-modal',
  templateUrl: './product-summary.component.html',
  styleUrls: ['./product-summary.component.css'],
})
export class ProductSummaryComponent implements OnInit {
  ProductType = ProductType;

  selectedVolume!: Volume | null;
  product!: IProduct;
  cart!: Cart;
  quantity!: number;
  subTotal!: number;

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
    this.product = this.data.product;
    this.cart = this.data.cart;
    this.quantity = this.data.quantity;
    this.subTotal = this.data.subTotal;
    this.selectedVolume = this.cart.selectedVolume || null; // Valeur par défaut null si non défini

    console.log('Product Data:', this.product);
    console.log('Cart Data:', this.cart);
    console.log('Selected Volume:', this.selectedVolume);
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

  goToShopping(): void {
    this.dialogRef.close();
    this.router.navigateByUrl('/');
  }
}

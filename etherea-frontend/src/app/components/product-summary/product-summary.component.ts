import { Component, Inject, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogConfig,
  MatDialogRef,
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Product } from '../models/Product.model';
import { ProductType } from '../models/productType.enum';
import { Cart } from '../models/cart.model';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { ProductVolume } from '../models/productVolume.model';

@Component({
  selector: 'app-product-summary-modal',
  templateUrl: './product-summary.component.html',
  styleUrls: ['./product-summary.component.css'],
})
export class ProductSummaryComponent implements OnInit {
  product!: Product;
  quantity!: number;
  selectedVolume!: ProductVolume | null;
  ProductType = ProductType;

  constructor(
    private dialogRef: MatDialogRef<ProductSummaryComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      product: Product;
      cart: Cart;
      quantity: number;
      subTotal: number;
    },
    private router: Router,
    public productTypeService: ProductTypeService
  ) {}

  ngOnInit(): void {
    this.updateDialogSize();

    if (!this.data.product) {
      console.error('Produit non défini dans les données du dialogue.');
      return;
    }

    this.product = this.data.product;
    this.quantity = this.data.quantity;

    if (this.data.cart) {
      this.selectedVolume = this.data.cart.selectedVolume || null;
      console.log('Cart Data:', this.data.cart);
      console.log('Selected Volume:', this.selectedVolume);
    } else {
      this.selectedVolume = null; // Par défaut à null si le panier est indéfini
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

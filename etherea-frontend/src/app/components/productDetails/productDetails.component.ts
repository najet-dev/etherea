import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, catchError, switchMap } from 'rxjs/operators';
import { IProduct } from '../models/i-product';
import { ProductService } from 'src/app/services/product.service';
import { ActivatedRoute } from '@angular/router';
import { CartService } from 'src/app/services/cart.service'; // Importez le service de panier
import { Cart } from '../models/cart.model';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit, OnDestroy {
  product: IProduct | null = null;

  cartItem: Cart = {
    userId: 1,
    productId: 1,
    quantity: 1,
    product: {
      id: 1,
      name: '',
      description: '',
      price: 0,
      stockAvailable: 0,
      benefits: '',
      usageTips: '',
      ingredients: '',
      characteristics: '',
      image: '',
    },
  };
  limitReached = false;

  private destroy$ = new Subject<void>();

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private cartService: CartService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.route.params
      .pipe(
        takeUntil(this.destroy$),
        switchMap((params) => {
          const id = params['id'];
          return this.productService.getProductById(id);
        }),
        catchError((error) => {
          console.error('Error fetching product:', error);
          console.error('Failed to load product. Please try again later.');
          throw error;
        })
      )
      .subscribe((product) => {
        this.product = product;
        this.cartItem.productId = product.id;
        this.cartItem.product = { ...product }; // Copiez les propriétés du produit dans cartItem.product
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  incrementQuantity(): void {
    if (this.cartItem.quantity < 8) {
      this.cartItem.quantity++;
    }
  }

  decrementQuantity(): void {
    if (this.cartItem.quantity > 1) {
      this.cartItem.quantity--;
    }
  }
  addToCart(): void {
    // Calcul de la quantité et du sous-total
    const subTotal = this.cartItem.quantity * this.cartItem.product.price;
    const quantity = this.cartItem.quantity;

    // Mise à jour des données du cartItem
    this.cartItem.subTotal = subTotal;

    this.cartService.addToCart(this.cartItem).subscribe(
      () => {
        console.log('Product added to cart');
        this.resetCartItem();

        // Ouvrir la modal avec les données mises à jour
        const dialogRef = this.dialog.open(ProductSummaryComponent, {
          width: '600px',
          height: '900px',
          data: {
            product: this.product,
            cart: this.cartItem,
            quantity: quantity, // Passer la quantité calculée
            subTotal: subTotal, // Passer le sous-total calculé
          },
        });

        dialogRef.afterClosed().subscribe((result) => {
          console.log('The dialog was closed');
        });
      },
      (error) => {
        console.error('Error adding product to cart:', error);
      }
    );
  }

  resetCartItem(): void {
    this.cartItem = {
      userId: 1,
      productId: 1,
      quantity: 1,
      product: {
        id: 0,
        name: '',
        description: '',
        price: 0,
        stockAvailable: 0,
        benefits: '',
        usageTips: '',
        ingredients: '',
        characteristics: '',
        image: '',
      },
    };
  }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, switchMap, catchError } from 'rxjs/operators';
import { IProduct } from '../models/i-product';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})

export class ProductDetailsComponent implements OnInit, OnDestroy {
  id: number = 0;
  product: IProduct = {
    id: 0,
    name: '',
    description: '',
    quantity: 1,
    price: 0,
    stockAvailable: 0,
    benefits: '',
    usageTips: '',
    ingredients: '',
    characteristics: '',
    image: '',
  };

  private destroy$ = new Subject<void>();
  limitReached: boolean = false;

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params
      .pipe(
        takeUntil(this.destroy$),
        switchMap((params) => {
          this.id = params['id'];
          return this.productService.getProductById(this.id);
        }),
        catchError((error) => {
          console.error('Error fetching product:', error);
          console.error('Failed to load product. Please try again later.');
          throw error;
        })
      )
      .subscribe((product) => {
        this.product = product;
        // Réinitialiser la quantité à 1 chaque fois que les détails du produit sont chargés
        this.product.quantity = 1;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    // Réinitialiser la quantité à 0 lors de la destruction du composant (quand on quitte la page)
    this.product.quantity = 1;
  }

  incrementQuantity(): void {
    if (this.product && this.product.quantity < 10) {
      this.productService
        .incrementProductQuantity(this.product.id)
        .subscribe((updatedProduct) => {
          this.product = updatedProduct;
          this.limitReached = false; // Réinitialiser la variable après l'incrémentation
        });
    } else {
      this.limitReached = true;
    }
  }

  decrementQuantity(): void {
    if (this.product && this.product.quantity > 1) {
      this.productService
        .decrementProductQuantity(this.product.id)
        .subscribe((updatedProduct) => {
          this.product = updatedProduct;
          this.limitReached = false; // Réinitialiser la variable après la décrémentation
        });
    }
  }

  addToCart(): void {
    if (this.product) {
      console.log(
        `Added ${this.product.quantity} ${this.product.name}(s) to the cart`
      );
    }
  }
}

export class ProductDetailsComponent {}


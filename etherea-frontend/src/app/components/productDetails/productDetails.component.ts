import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, switchMap, catchError } from 'rxjs/operators';
import { IProduct } from '../models/i-product';
import { ProductService } from 'src/app/services/product.service';
import { ActivatedRoute } from '@angular/router';

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
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

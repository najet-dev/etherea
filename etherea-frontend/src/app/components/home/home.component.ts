import { Component } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { IProduct } from '../models/i-product';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {
  products: IProduct[] = [];

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.productService.getProducts().subscribe(
      (data) => {
        this.products = data;
      },
      (error) => {
        console.error(
          "Une erreur s'est produite lors de la récupération des produits.",
          error
        );
      }
    );
  }
}

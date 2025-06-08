import { Component, DestroyRef, inject } from '@angular/core';
import { Product } from '../../models';
import {
  catchError,
  Observable,
  of,
  switchMap,
  tap,
  debounceTime,
  distinctUntilChanged,
} from 'rxjs';
import { ProductService } from 'src/app/services/product.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl } from '@angular/forms';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent {
  products: Product[] = [];
  currentPage: number = 0;
  totalPages: number = 1;
  pageSize: number = 10;
  searchQuery: string = '';
  searchControl = new FormControl('');

  private destroyRef = inject(DestroyRef);

  constructor(private appFacade: AppFacade) {}

  ngOnInit(): void {
    this.loadProducts();

    // Ajoute un écouteur pour l'input de recherche avec debounce
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300), // Attendre 300ms après la dernière frappe
        distinctUntilChanged(), // Ne pas relancer la requête si la recherche n'a pas changé
        tap(() => this.searchProducts()),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  loadProducts(): void {
    this.appFacade
      .getAllProducts(this.currentPage, this.pageSize)
      .pipe(
        tap((response) => {
          this.products = response.content;
          this.totalPages = response.totalPages;
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération des produits:', error);
          this.products = [];
          return of([]);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  searchProducts(): void {
    if (this.searchQuery.trim().length > 1) {
      this.appFacade.searchProductsByName(this.searchQuery).subscribe(
        (results) => {
          this.products = results;
        },
        (error) => {
          console.error('Erreur lors de la recherche:', error);
          this.products = [];
        }
      );
    } else {
      this.loadProducts(); // Recharger tous les produits si la recherche est vide
    }
  }

  deleteProduct(productId: number): void {
    this.appFacade
      .deleteProduct(productId)
      .pipe(
        switchMap(() =>
          this.appFacade.getAllProducts(this.currentPage, this.pageSize)
        ),
        catchError((error) => {
          console.error('Erreur lors de la suppression du produit:', error);
          return of({ content: [], totalElements: 0, totalPages: 1 });
        })
      )
      .subscribe((response) => {
        this.products = response.content;
        this.totalPages = response.totalPages;
      });
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }
}

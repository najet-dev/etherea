import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  switchMap,
  catchError,
} from 'rxjs/operators';
import { Product } from '../components/models';
import { ProductService } from './product.service';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private searchQuerySubject = new Subject<string>(); // Sujet pour écouter les changements de recherche
  private searchResultsSubject = new BehaviorSubject<Product[]>([]);
  searchResults$ = this.searchResultsSubject.asObservable();

  constructor(private productService: ProductService) {
    this.searchQuerySubject
      .pipe(
        debounceTime(300), // Attendre 300ms après la dernière frappe
        distinctUntilChanged(), // Ne pas relancer la requête si la recherche n'a pas changé
        switchMap(
          (query) =>
            query.trim().length > 1
              ? this.productService.searchProductsByName(query) // Effectuer la recherche si le texte est valide
              : [] // Sinon, renvoyer une liste vide
        ),
        catchError((error) => {
          console.error('Erreur de recherche:', error);
          return [];
        })
      )
      .subscribe((results) => this.searchResultsSubject.next(results));
  }

  searchProducts(query: string): void {
    this.searchQuerySubject.next(query);
  }

  clearSearch(): void {
    this.searchResultsSubject.next([]); // Réinitialiser les résultats
  }
}

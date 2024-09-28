import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { IProduct } from '../models/i-product.model';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { AppFacade } from 'src/app/services/appFacade.service';

import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  products$: Observable<IProduct[]> = new Observable<IProduct[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef); // Inject DestroyRef

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.authService
      .getCurrentUser()
      .pipe(
        tap((user) => (this.userId = user ? user.id : null)),
        takeUntilDestroyed(this.destroyRef) // Use takeUntilDestroyed
      )
      .subscribe();
  }

  loadProducts(): void {
    this.products$ = this.appFacade.getProducts(12).pipe(
      switchMap((products) => this.appFacade.productsFavorites(products)),
      catchError((error) => {
        console.error('Error fetching products:', error);
        return of([]);
      }),
      takeUntilDestroyed(this.destroyRef) // Use takeUntilDestroyed
    );
  }

  handleFavoriteClick(product: IProduct): void {
    if (this.userId === null) {
      this.router.navigate(['/signin']);
    } else {
      this.toggleFavorite(product);
    }
  }

  toggleFavorite(product: IProduct): void {
    this.appFacade.toggleFavorite(product);
  }
}

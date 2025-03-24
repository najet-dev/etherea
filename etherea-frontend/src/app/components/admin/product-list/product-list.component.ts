import { Component, DestroyRef, inject } from '@angular/core';
import { Product } from '../../models';
import { catchError, Observable, of, switchMap, tap } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Router } from '@angular/router';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent {
  products$: Observable<Product[]> = new Observable<Product[]>();
  userId: number | null = null;
  private destroyRef = inject(DestroyRef);

  constructor(
    private authService: AuthService,
    private appFacade: AppFacade,
    private router: Router,
    public productTypeService: ProductTypeService
  ) {}
  ngOnInit(): void {
    this.loadProducts();
    this.authService
      .getCurrentUser()
      .pipe(
        tap((user) => (this.userId = user ? user.id : null)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  loadProducts(): void {
    this.products$ = this.appFacade.getProducts().pipe(
      catchError((error) => {
        console.error('Error fetching products:', error);
        return of([]);
      }),
      takeUntilDestroyed(this.destroyRef)
    );
  }
}

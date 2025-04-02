import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PageEvent } from '@angular/material/paginator';
import { tap, catchError, of, switchMap } from 'rxjs';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Volume } from '../../models/volume.model';
import { Tip } from '../../models/tip.model';

@Component({
  selector: 'app-tip-list',
  templateUrl: './tip-list.component.html',
  styleUrls: ['./tip-list.component.css'],
})
export class TipListComponent {
  tips: Tip[] = [];
  totalPages: number = 0;
  totalElements: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  private destroyRef = inject(DestroyRef);

  constructor(private appFacade: AppFacade) {}

  ngOnInit(): void {
    this.loadTips();
  }

  loadTips(page: number = 0): void {
    this.appFacade
      .getAllTips(page, this.pageSize)
      .pipe(
        tap((response) => {
          this.tips = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.currentPage = page;
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération des volumes:', error);
          this.tips = [];
          return of([]);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  onPageChanged(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadTips(this.currentPage); // Charger les volumes pour la page sélectionnée
  }
  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTips(this.currentPage);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTips(this.currentPage);
    }
  }
}

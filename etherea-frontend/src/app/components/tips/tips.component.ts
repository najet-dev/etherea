import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { TipService } from 'src/app/services/tip.service';
import { Tip } from '../models/tip.model';
import { PageEvent } from '@angular/material/paginator';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of, tap } from 'rxjs';

@Component({
  selector: 'app-tips',
  templateUrl: './tips.component.html',
  styleUrls: ['./tips.component.css'],
})
export class TipsComponent implements OnInit {
  tips: Tip[] = [];
  totalPages: number = 0;
  totalElements: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  private destroyRef = inject(DestroyRef);

  constructor(private tipService: TipService) {}

  ngOnInit(): void {
    this.loadTips();
  }

  loadTips(page: number = 0): void {
    this.tipService
      .getAlltips(page, this.pageSize)
      .pipe(
        tap((response) => {
          this.tips = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.currentPage = page;
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération des conseils:', error);
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
    this.loadTips(this.currentPage);
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

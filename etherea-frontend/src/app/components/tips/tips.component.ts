import { Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { TipService } from 'src/app/services/tip.service';
import { catchError, of, tap } from 'rxjs';
import { Tip } from '../models/tip.model';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-tips',
  templateUrl: './tips.component.html',
  styleUrls: ['./tips.component.css'],
})
export class TipsComponent implements OnInit {
  @Input() showPagination: boolean = true; // Permet d'afficher ou non la pagination
  @Input() showBreadcrumb: boolean = true; // Ajout pour afficher ou non le breadcrumb
  @Input() limit?: number;

  tips: Tip[] = [];
  totalPages: number = 0;
  totalElements: number = 0;
  currentPage: number = 0;
  pageSize: number = 6;

  private destroyRef = inject(DestroyRef);

  constructor(private appFacade: AppFacade) {}

  ngOnInit(): void {
    if (this.limit) {
      this.loadLimitedTips();
    } else {
      this.loadTips();
    }
  }

  loadTips(page: number = 0): void {
    this.appFacade
      .getAllTips(page, this.pageSize)
      .pipe(
        tap((response) => {
          console.log('Réponse API:', response);
          this.tips = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.currentPage = page;
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération des conseils:', error);
          this.tips = [];
          return of([]);
        })
      )
      .subscribe();
  }

  loadLimitedTips(): void {
    this.appFacade
      .getAllTips(0, 1000)
      .pipe(
        tap((response) => {
          this.tips = response.content.slice(0, this.limit);
        }),
        catchError((error) => {
          console.error(
            'Erreur lors du chargement des conseils (limités) :',
            error
          );
          this.tips = [];
          return of([]);
        })
      )
      .subscribe();
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

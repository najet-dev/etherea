import { Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { TipService } from 'src/app/services/tip.service';
import { Tip } from '../models/tip.model';
import { catchError, of, tap } from 'rxjs';

@Component({
  selector: 'app-tips',
  templateUrl: './tips.component.html',
  styleUrls: ['./tips.component.css'],
})
export class TipsComponent implements OnInit {
  @Input() showPagination: boolean = true; // Permet d'afficher ou non la pagination
  @Input() showBreadcrumb: boolean = true; // Ajout pour afficher ou non le breadcrumb

  tips: Tip[] = [];
  totalPages: number = 0;
  totalElements: number = 0;
  currentPage: number = 0;
  pageSize: number = 3;
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

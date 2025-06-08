import { Component, DestroyRef, inject } from '@angular/core';
import { Volume } from '../../models/volume.model';
import { catchError, of, switchMap, tap } from 'rxjs';
import { AppFacade } from 'src/app/services/appFacade.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-volume-list',
  templateUrl: './volume-list.component.html',
  styleUrls: ['./volume-list.component.css'],
})
export class VolumeListComponent {
  volumes: Volume[] = [];
  totalPages: number = 0;
  totalElements: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  private destroyRef = inject(DestroyRef);

  constructor(private appFacade: AppFacade) {}

  ngOnInit(): void {
    this.loadVolumes();
  }

  loadVolumes(page: number = 0): void {
    this.appFacade
      .getAllVolumes(page, this.pageSize)
      .pipe(
        tap((response) => {
          this.volumes = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.currentPage = page;
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération des volumes:', error);
          this.volumes = [];
          return of([]);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  onPageChanged(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadVolumes(this.currentPage); // Charger les volumes pour la page sélectionnée
  }
  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadVolumes(this.currentPage);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadVolumes(this.currentPage);
    }
  }

  deleteVolume(volumeId: number): void {
    this.appFacade
      .deleteVolume(volumeId)
      .pipe(
        switchMap(() =>
          this.appFacade.getAllVolumes(this.currentPage, this.pageSize)
        ),
        catchError((error) => {
          console.error('Erreur lors de la suppression du volume:', error);
          return of({ content: [], totalElements: 0, totalPages: 0 });
        })
      )
      .subscribe((response) => {
        this.volumes = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
      });
  }
}

import { Component, DestroyRef, inject } from '@angular/core';
import { Volume } from '../../models/volume.model';
import { VolumeService } from 'src/app/services/volume.service';
import { catchError, of, switchMap, tap } from 'rxjs';
import { AppFacade } from 'src/app/services/appFacade.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-volume-list',
  templateUrl: './volume-list.component.html',
  styleUrls: ['./volume-list.component.css'],
})
export class VolumeListComponent {
  volumes: Volume[] = [];
  private destroyRef = inject(DestroyRef);

  constructor(
    private appFacade: AppFacade,
    private volumeService: VolumeService
  ) {}

  ngOnInit(): void {
    this.loadVolumes();
  }

  loadVolumes(): void {
    this.appFacade
      .getVolumes()
      .pipe(
        tap((volumes) => {
          if (Array.isArray(volumes)) {
            this.volumes = volumes;
          } else {
            console.error('Données invalides reçues :', volumes);
            this.volumes = [];
          }
        }),
        catchError((error) => {
          console.error('Erreur lors de la récupération des volumes:', error);
          this.volumes = [];
          return of([]);
        }),
        takeUntilDestroyed(this.destroyRef) // Annuler la requête si le composant est détruit
      )
      .subscribe();
  }

  deleteVolume(volumeId: number): void {
    this.volumeService
      .deleteVolume(volumeId)
      .pipe(
        switchMap(() => this.appFacade.getVolumes()), // Recharger la liste des volumes après la suppression
        catchError((error) => {
          console.error('Erreur lors de la suppression du volume:', error);
          return of([]);
        })
      )
      .subscribe((volumes) => {
        this.volumes = volumes; // Mettre à jour la liste des volumes affichée
      });
  }
}

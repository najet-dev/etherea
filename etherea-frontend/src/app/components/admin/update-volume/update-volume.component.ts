import { Component } from '@angular/core';
import { VolumeService } from 'src/app/services/volume.service';
import { Volume } from '../../models/volume.model';

@Component({
  selector: 'app-update-volume',
  templateUrl: './update-volume.component.html',
  styleUrls: ['./update-volume.component.css'],
})
export class UpdateVolumeComponent {
  updateVolume: Volume = {
    id: 0,
    productName: '',
    volume: 0,
    price: 0,
  };
  successMessage = '';

  constructor(private volumeService: VolumeService) {}

  onSubmit(): void {
    {
      this.volumeService
        .updatedVolume(this.updateVolume.id, this.updateVolume)
        .subscribe({
          next: (response) => {
            console.log('Volume ajouté avec succès:', response);
            this.successMessage = 'Volume ajouté avec succès.';
            this.resetForm();
          },
          error: (error) => {
            console.error("Erreur lors de l'ajout du volume:", error);
          },
          complete: () => {
            console.log('Ajout du volume terminé.');
          },
        });
    }
  }
  resetForm(): void {
    this.updateVolume = {
      id: 0,
      productName: '',
      volume: 0,
      price: 0,
    };
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }
}

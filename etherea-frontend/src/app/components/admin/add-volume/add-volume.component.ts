import { Component } from '@angular/core';
import { Volume } from '../../models/volume.model';
import { VolumeService } from 'src/app/services/volume.service';

@Component({
  selector: 'app-add-volume',
  templateUrl: './add-volume.component.html',
  styleUrls: ['./add-volume.component.css'],
})
export class AddVolumeComponent {
  volume: Volume = {
    id: 0,
    productName: '',
    volume: 0,
    price: 0,
  };
  successMessage = '';

  constructor(private volumeService: VolumeService) {}

  onSubmit(): void {
    if (
      this.volume.productName &&
      this.volume.volume > 0 &&
      this.volume.price > 0
    ) {
      this.volumeService.addVolume(this.volume).subscribe({
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
    } else {
      console.log(
        'Erreur: Veuillez remplir toutes les informations nécessaires.'
      );
    }
  }
  resetForm(): void {
    this.volume = {
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

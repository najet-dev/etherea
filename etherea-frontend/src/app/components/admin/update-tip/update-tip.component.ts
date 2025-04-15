import { Component } from '@angular/core';
import { Tip } from '../../models/tip.model';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-update-tip',
  templateUrl: './update-tip.component.html',
  styleUrls: ['./update-tip.component.css'],
})
export class UpdateTipComponent {
  updateTip: Tip = {
    id: 0,
    title: '',
    description: '',
    content: '',
    image: '',
    dateCreation: '',
  };
  successMessage = '';
  errorMessage = '';
  selectedFile: File | null = null;

  constructor(private appFacade: AppFacade) {}

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }
  onSubmit(): void {
    this.appFacade.updateTip(this.updateTip).subscribe({
      next: (response) => {
        console.log('Produit modifié avec succès:', response);
        this.successMessage = 'Produit mis à jour avec succès.';
        this.resetForm();
      },
      error: (error) => {
        console.error('Erreur lors de la mise à jour du produit:', error);
      },
      complete: () => {
        console.log('Mise à jour du produit terminée.');
      },
    });
  }

  resetForm(): void {
    this.updateTip = {
      id: 0,
      title: '',
      description: '',
      content: '',
      image: '',
      dateCreation: '',
    };
    setTimeout(() => {
      this.successMessage = '';
      this.errorMessage = '';
    }, 3000);
  }
}

import { Component } from '@angular/core';
import { TipService } from 'src/app/services/tip.service';
import { Tip } from '../../models/tip.model';

@Component({
  selector: 'app-add-tip',
  templateUrl: './add-tip.component.html',
  styleUrls: ['./add-tip.component.css'],
})
export class AddTipComponent {
  tip: Tip = {
    id: 0,
    title: '',
    description: '',
    content: '',
    image: '',
    dateCreation: '',
  };
  successMessage = '';
  selectedFile: File | null = null;

  constructor(private tipService: TipService) {}

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit(): void {
    if (
      !this.tip.title ||
      !this.tip.description ||
      !this.tip.content ||
      !this.selectedFile
    ) {
      console.error('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    this.tipService.addTip(this.tip, this.selectedFile).subscribe({
      next: (response) => {
        console.log('Conseil ajouté avec succès:', response);
        this.successMessage = 'Conseil ajouté avec succès.';
        this.resetForm();
      },
      error: (error) => {
        console.error("Erreur lors de l'ajout du conseil :", error);
      },
      complete: () => {
        console.log('Ajout du conseil terminé.');
      },
    });
  }

  resetForm(): void {
    this.tip = {
      id: 0,
      title: '',
      description: '',
      content: '',
      image: '',
      dateCreation: '',
    };
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }
}

import { Component } from '@angular/core';
import { Product, ProductType } from '../../models';
import { ProductService } from 'src/app/services/product.service';
import { StockStatus } from '../../models/stock-status.enum';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-add-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.css'],
})
export class AddProductComponent {
  product: Product = {
    id: 0,
    name: '',
    description: '',
    type: ProductType.FACE,
    benefits: '',
    usageTips: '',
    ingredients: '',
    basePrice: 0,
    characteristics: '',
    stockQuantity: 1,
    stockStatus: StockStatus.AVAILABLE,
    image: '',
    newProduct: false,
  };
  ProductType = ProductType;
  selectedFile: File | null = null;
  StockStatus = StockStatus;
  successMessage = ''; // Variable pour le message de succès

  constructor(private appFacade: AppFacade) {}

  // Méthode pour traiter le changement d'image
  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit(): void {
    if (!this.product.name || !this.product.type || !this.selectedFile) {
      console.error('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    this.appFacade.addProduct(this.product, this.selectedFile).subscribe({
      next: (response) => {
        console.log('Produit ajouté avec succès:', response);
        this.successMessage = 'Produit ajouté avec succès.';
        this.resetForm();
      },
      error: (error) => {
        console.error("Erreur lors de l'ajout du produit:", error);
      },
      complete: () => {
        console.log('Ajout du produit terminé.');
      },
    });
  }

  // Méthode pour réinitialiser le formulaire après ajout
  resetForm(): void {
    this.product = {
      id: 0,
      name: '',
      description: '',
      type: ProductType.FACE,
      benefits: '',
      usageTips: '',
      ingredients: '',
      basePrice: 0,
      characteristics: '',
      stockQuantity: 1,
      stockStatus: StockStatus.AVAILABLE,
      image: '',
      newProduct: false,
    };
    this.selectedFile = null;
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }
}

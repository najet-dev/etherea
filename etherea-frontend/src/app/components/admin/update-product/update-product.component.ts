import { Component } from '@angular/core';
import { Product, ProductType } from '../../models';
import { StockStatus } from '../../models/stock-status.enum';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-update-product',
  templateUrl: './update-product.component.html',
  styleUrls: ['./update-product.component.css'],
})
export class UpdateProductComponent {
  updateProduct: Product = {
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
  successMessage = '';

  constructor(private productService: ProductService) {}

  // Méthode pour traiter le changement d'image
  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit(): void {
    if (!this.updateProduct.name || !this.updateProduct.type) {
      console.error('Veuillez remplir les champs obligatoires.');
      return;
    }

    this.productService.updateProduct(this.updateProduct).subscribe({
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

  // Méthode pour réinitialiser le formulaire après ajout
  resetForm(): void {
    this.updateProduct = {
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

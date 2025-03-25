import { Component } from '@angular/core';
import { Product, ProductType } from '../../models';
import { StockStatus } from '../../models/stock-status.enum';
import { ProductService } from 'src/app/services/product.service';
import { UpdateProduct } from '../../models/updateProduct.model';

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
  };
  ProductType = ProductType;
  selectedFile: File | null = null;
  StockStatus = StockStatus;

  constructor(private productService: ProductService) {}

  // Méthode pour traiter le changement d'image
  // Gestion du fichier sélectionné
  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  // Méthode pour envoyer les données du produit au backend
  onSubmit(): void {
    if (
      !this.updateProduct.name ||
      !this.updateProduct.type ||
      !this.selectedFile
    ) {
      console.error('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    this.productService
      .updateProduct(this.updateProduct, this.selectedFile)
      .subscribe({
        next: (response) => {
          console.log('Produit ajouté avec succès:', response);
        },
        error: (error) => {
          console.error("Erreur lors de l'ajout du produit:", error);
        },
        complete: () => {
          console.log('Ajout du produit terminé.');
        },
      });
  }
}

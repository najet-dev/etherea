import { Component } from '@angular/core';
import { Product, ProductType } from '../../models';
import { ProductService } from 'src/app/services/product.service';
import { StockStatus } from '../../models/stock-status.enum';

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
    if (!this.product.name || !this.product.type || !this.selectedFile) {
      console.error('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    this.productService.addProduct(this.product, this.selectedFile).subscribe({
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

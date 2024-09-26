import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { switchMap, catchError, of } from 'rxjs';
import { IProduct, ProductType } from '../models/i-product';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from 'src/app/services/auth.service';
import { SigninRequest } from '../models/signinRequest.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppFacade } from 'src/app/services/appFacade.service';
import { Volume } from '../models/volume.model';
import { Cart } from '../models/cart.model';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit {
  product: IProduct | null = null;
  selectedVolume: Volume | null = null; // Initialiser à null
  userId: number | null = null;
  cartItem: Cart = {
    id: 0,
    userId: 0,
    productId: 0,
    quantity: 1,
    product: {
      id: 0,
      name: '',
      description: '',
      type: ProductType.HAIR,
      stockStatus: '',
      benefits: '',
      usageTips: '',
      ingredients: '',
      characteristics: '',
      image: '',
      volumes: [],
      basePrice: 0,
    },
    selectedVolume: null, // Modifier à null
  };

  limitReached = false;
  stockMessage: string = '';
  private destroyRef = inject(DestroyRef);

  constructor(
    private route: ActivatedRoute,
    private appFacade: AppFacade,
    private dialog: MatDialog,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadProductDetails();
    this.loadCurrentUser();
  }

  loadProductDetails(): void {
    this.route.params
      .pipe(
        switchMap((params) => {
          const id = params['id'];
          return this.appFacade.getProductById(id);
        }),
        catchError((error) => {
          console.error('Error fetching product:', error);
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((product) => {
        if (product) {
          this.product = product;
          this.cartItem.productId = product.id;
          this.cartItem.product = { ...product };

          // Afficher le prix du volume sélectionné si c'est un produit de type HAIR
          if (product.type === ProductType.HAIR && product.volumes?.length) {
            this.selectedVolume = product.volumes[0]; // Par exemple, le premier volume
            console.log('Volume sélectionné:', this.selectedVolume);
          }

          this.updateStockMessage(product.stockStatus);
          console.log('Produit chargé:', product);
        }
      });
  }

  loadCurrentUser(): void {
    this.authService
      .getCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user: SigninRequest | null) => {
          if (user) {
            console.log('Utilisateur connecté:', user);
            this.userId = user.id;
          } else {
            console.log('Aucun utilisateur connecté');
            this.userId = null;
          }
        },
        error: (error) => {
          console.error(
            'Erreur lors de la récupération de l’utilisateur:',
            error
          );
        },
      });
  }

  updateStockMessage(stockStatus: string): void {
    switch (stockStatus) {
      case 'AVAILABLE':
        this.stockMessage = 'Le produit est disponible.';
        break;
      case 'OUT_OF_STOCK':
        this.stockMessage = 'Le produit est actuellement en rupture de stock.';
        break;
      default:
        this.stockMessage = 'Le statut du stock du produit est inconnu.';
    }
  }

  // Gérer le changement de volume
  onVolumeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const selectedValue = target?.value;

    if (selectedValue && this.product?.volumes) {
      const volume = this.product.volumes.find(
        (vol) => vol.volume.toString() === selectedValue
      );

      if (volume) {
        this.selectedVolume = volume; // Assurez-vous que le volume sélectionné est bien mis à jour ici
        console.log('Volume sélectionné:', this.selectedVolume);
      } else {
        console.error(
          'Volume sélectionné introuvable dans les volumes du produit.'
        );
      }
    } else {
      console.error(
        'Sélection de volume invalide ou volumes du produit non disponibles.'
      );
    }
  }

  incrementQuantity(): void {
    if (this.cartItem.quantity < 10) {
      this.cartItem.quantity++;
    }
  }

  decrementQuantity(): void {
    if (this.cartItem.quantity > 1) {
      this.cartItem.quantity--;
    }
  }

  // Ajouter un article au panier
  // Ajouter un article au panier
  addToCart(): void {
    if (!this.userId) {
      console.error('ID utilisateur non disponible.');
      alert('Vous devez être connecté pour ajouter des articles au panier.');
      return;
    }

    // Vérification si un volume est sélectionné pour les produits HAIR
    if (this.product?.type === ProductType.HAIR && !this.selectedVolume) {
      console.error('Aucun volume sélectionné pour un produit HAIR.');
      alert("Veuillez sélectionner un volume avant d'ajouter au panier.");
      return;
    }

    // Assurez-vous que le bon volume est assigné à l'objet cartItem
    this.cartItem.userId = this.userId;
    this.cartItem.selectedVolume = this.selectedVolume
      ? { ...this.selectedVolume } // Cloner l'objet selectedVolume pour éviter les références directes
      : null;

    // Log pour vérifier que le volume est bien assigné
    console.log(
      'Ajout au panier avec le volume:',
      this.cartItem.selectedVolume
    );

    this.appFacade
      .addToCart(this.cartItem)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          console.log('Produit ajouté au panier avec succès');
          this.openProductSummaryDialog();
          this.resetCartItem();
        },
        error: (error) => {
          console.error('Erreur lors de l’ajout du produit au panier :', error);
          alert(
            error.error?.message ||
              "Une erreur est survenue lors de l'ajout au panier."
          );
        },
      });
  }

  openProductSummaryDialog(): void {
    const dialogRef = this.dialog.open(ProductSummaryComponent, {
      width: '600px',
      height: '900px',
      data: {
        product: this.product,
        cart: this.cartItem,
        quantity: this.cartItem.quantity,
        subTotal: this.cartItem.subTotal,
        selectedVolume: this.cartItem.selectedVolume?.volume || null,
      },
    });

    dialogRef.afterClosed().subscribe(() => {
      console.log('Le dialogue a été fermé');
    });
  }

  private resetCartItem(): void {
    this.cartItem = {
      id: 0,
      userId: 0,
      productId: 0,
      quantity: 1,
      product: {
        id: 0,
        name: '',
        description: '',
        type: ProductType.HAIR,
        stockStatus: '',
        benefits: '',
        usageTips: '',
        ingredients: '',
        characteristics: '',
        image: '',
        volumes: [],
        basePrice: 0,
      },
      selectedVolume: null, // Modifier à null
    };
    this.selectedVolume = null;
  }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, catchError, switchMap } from 'rxjs/operators';
import { IProduct, ProductType } from '../models/i-product';
import { ProductService } from 'src/app/services/product.service';
import { ActivatedRoute } from '@angular/router';
import { CartService } from 'src/app/services/cart.service';
import { Cart } from '../models/cart.model';
import { ProductSummaryComponent } from '../product-summary/product-summary.component';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from 'src/app/services/auth.service'; // Importez le service d'authentification
import { SigninRequest } from '../models/signinRequest.model'; // Importez le modèle SigninRequest

@Component({
  selector: 'app-product-details',
  templateUrl: './productDetails.component.html',
  styleUrls: ['./productDetails.component.css'],
})
export class ProductDetailsComponent implements OnInit, OnDestroy {
  // Propriété pour stocker les détails du produit
  product: IProduct | null = null;
  // Propriété pour stocker l'ID de l'utilisateur actuel
  userId: number | null = null;

  // Propriété pour stocker les détails de l'article dans le panier
  cartItem: Cart = {
    id: 0,
    userId: 0,
    productId: 1,
    quantity: 1,
    // Propriété pour stocker les détails du produit associé à l'article du panier
    product: {
      id: 1,
      name: '',
      description: '',
      price: 0,
      type: '',
      stockStatus: '',
      benefits: '',
      usageTips: '',
      ingredients: '',
      characteristics: '',
      image: '',
    },
  };
  // Indicateur pour vérifier si la limite de quantité a été atteinte
  limitReached = false;

  // Message concernant le statut du stock du produit
  stockMessage: string = '';

  // Observable utilisé pour détruire les abonnements lors de la destruction du composant
  private destroy$ = new Subject<void>();

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private cartService: CartService,
    private dialog: MatDialog,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Récupérer les détails du produit lors de l'initialisation du composant
    this.route.params
      .pipe(
        takeUntil(this.destroy$), // Se désabonner lorsque le composant est détruit
        switchMap((params) => {
          const id = params['id']; // Récupérer l'ID du produit depuis les paramètres de l'URL
          return this.productService.getProductById(id); // Appel du service pour récupérer les détails du produit
        }),
        catchError((error) => {
          console.error('Error fetching product:', error); // Gérer les erreurs lors de la récupération du produit
          console.error('Failed to load product. Please try again later.');
          throw error;
        })
      )
      .subscribe((product) => {
        // Mettre à jour les détails du produit une fois récupérés avec succès
        this.product = product;
        this.cartItem.productId = product.id;
        this.cartItem.product = { ...product };

        // Déterminer le message concernant le statut du stock du produit
        if (product.stockStatus === 'AVAILABLE') {
          this.stockMessage = `Le produit est disponible.`;
        } else if (product.stockStatus === 'OUT_OF_STOCK') {
          this.stockMessage = `Le produit est actuellement en rupture de stock.`;
        } else {
          this.stockMessage = 'Le statut du stock du produit est inconnu.';
        }
      });

    // Récupérer l'ID de l'utilisateur actuel lors de l'initialisation du composant
    this.authService.getCurrentUser().subscribe({
      next: (user: SigninRequest | null) => {
        this.userId = user ? user.id : null; // Affecter l'ID de l'utilisateur actuel ou null
      },
      error: (error) => {
        console.error('Error getting current user ID:', error); // Gérer les erreurs lors de la récupération de l'ID de l'utilisateur
      },
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next(); // Indiquer la destruction du composant
    this.destroy$.complete(); // Compléter la destruction du composant
  }

  // Méthode pour incrémenter la quantité de l'article dans le panier
  incrementQuantity(): void {
    if (this.cartItem.quantity < 8) {
      this.cartItem.quantity++;
    }
  }

  // Méthode pour décrémenter la quantité de l'article dans le panier
  decrementQuantity(): void {
    if (this.cartItem.quantity > 1) {
      this.cartItem.quantity--;
    }
  }

  // Méthode pour ajouter l'article au panier
  addToCart(): void {
    if (this.userId !== null) {
      const subTotal = this.cartItem.quantity * this.cartItem.product.price;
      const quantity = this.cartItem.quantity;

      this.cartItem.subTotal = subTotal;
      this.cartItem.userId = this.userId;

      // Appel du service pour ajouter l'article au panier
      this.cartService.addToCart(this.cartItem).subscribe(
        () => {
          console.log('Product added to cart'); // Afficher un message de confirmation
          this.resetCartItem(); // Réinitialiser les détails de l'article dans le panier

          // Afficher une boîte de dialogue avec le résumé du produit ajouté
          const dialogRef = this.dialog.open(ProductSummaryComponent, {
            width: '600px',
            height: '900px',
            data: {
              product: this.product,
              cart: this.cartItem,
              quantity: quantity,
              subTotal: subTotal,
            },
          });

          // Souscrire à la fermeture de la boîte de dialogue
          dialogRef.afterClosed().subscribe((result) => {
            console.log('The dialog was closed'); // message lorsque la boîte de dialogue est fermée
          });
        },
        (error) => {
          console.error('Error adding product to cart:', error); // Gérer les erreurs lors de l'ajout du produit au panier
        }
      );
    } else {
      console.error('User ID is not available.'); // message si l'ID de l'utilisateur n'est pas disponible
    }
  }

  // Méthode pour réinitialiser les détails de l'article dans le panier
  resetCartItem(): void {
    this.cartItem = {
      id: 0,
      userId: 0,
      productId: 1,
      quantity: 1,
      product: {
        id: 0,
        name: '',
        description: '',
        price: 0,
        type: '',
        stockStatus: '',
        benefits: '',
        usageTips: '',
        ingredients: '',
        characteristics: '',
        image: '',
      },
    };
  }
}

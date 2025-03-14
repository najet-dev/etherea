import { Component } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { Newsletter } from '../models/newsletter.model';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
})
export class FooterComponent {
  email: string = ''; // Variable pour stocker l'email de l'utilisateur
  message: string | null = null; // Message de confirmation ou d'erreur

  constructor(private userService: UserService) {}
  toggleSection(targetId: string) {
    const target = document.getElementById(targetId);
    const icon = document.querySelector(`#${targetId}-icon`);

    if (target && icon) {
      // Basculer l'affichage de la section
      target.style.display =
        target.style.display === 'block' ? 'none' : 'block';

      // Ajouter/supprimer la classe pour la rotation de l'icône
      icon.classList.toggle('rotate');
    }
  }
  onSubscribe() {
    if (!this.email.trim()) {
      this.message = 'Veuillez entrer une adresse e-mail valide.';
      return;
    }

    const newsletter: Newsletter = { id: 0, email: this.email };

    this.userService.subscribeToNewsletter(newsletter).subscribe({
      next: (response) => {
        this.message = response.message;
        this.email = '';

        setTimeout(() => {
          this.message = null;
        }, 3000);
      },
      error: (error) => {
        this.message = error.message || 'Cet utilisateur est déjà inscrit';

        setTimeout(() => {
          this.message = null;
        }, 3000);
      },
    });
  }
}

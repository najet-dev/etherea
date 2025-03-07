import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
})
export class FooterComponent {
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
}

import { Component } from '@angular/core';
import { Contact } from '../models/contact.model';
import { ContactServiceService } from 'src/app/services/contact-service.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css'],
})
export class ContactComponent {
  contact: Contact = {
    firstName: '',
    lastName: '',
    email: '',
    subject: '',
    message: '',
  };

  successMessage: string = '';
  errorMessage: string = '';
  formSubmitted: boolean = false;

  constructor(private contactService: ContactServiceService) {}

  sendMessage() {
    this.contactService.sendMessage(this.contact).subscribe({
      next: () => {
        this.successMessage =
          'Votre message a été transmis avec succès ! Vous recevrez une réponse sous 24 heures.';
        this.errorMessage = '';
        this.formSubmitted = true; // Cache le formulaire après l'envoi
      },
      error: (error) => {
        this.successMessage = '';
        this.errorMessage =
          'Une erreur est survenue. Veuillez réessayer plus tard.';
        console.error('Erreur:', error);
      },
    });
  }
}

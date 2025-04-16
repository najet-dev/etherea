import { Component } from '@angular/core';
import { NewsletterSend } from '../../models/newsletterSend.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-newsletter-admin',
  templateUrl: './newsletter-admin.component.html',
  styleUrls: ['./newsletter-admin.component.css'],
})
export class NewsletterAdminComponent {
  newsletterForm: FormGroup;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  constructor(private fb: FormBuilder, private appFacade: AppFacade) {
    this.newsletterForm = this.fb.group({
      subject: ['', [Validators.required, Validators.minLength(3)]],
      content: ['', [Validators.required, Validators.minLength(10)]],
    });
  }

  sendNewsletter(): void {
    if (this.newsletterForm.invalid) {
      this.newsletterForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.successMessage = '';
    this.errorMessage = '';

    const newsletterSend: NewsletterSend = this.newsletterForm.value;

    this.appFacade.sendNewsletter(newsletterSend).subscribe({
      next: (response) => {
        this.successMessage = response.message;
        this.newsletterForm.reset();
        this.isSubmitting = false;

        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (err) => {
        this.errorMessage = err.message;
        this.isSubmitting = false;
      },
    });
  }
}

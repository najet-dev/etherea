import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TipService } from 'src/app/services/tip.service';
import { Tip } from '../models/tip.model';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-tip-detail',
  templateUrl: './tip-detail.component.html',
  styleUrls: ['./tip-detail.component.css'],
})
export class TipDetailComponent implements OnInit {
  tips!: Tip;
  formattedContent!: SafeHtml;
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private tipService: TipService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.tipService.getTipById(+id).subscribe({
        next: (data) => {
          this.tips = data;
          this.formattedContent = this.sanitizer.bypassSecurityTrustHtml(
            this.tips.content
          );
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Erreur lors du chargement du conseil', err);
          this.isLoading = false;
        },
      });
    }
  }
}

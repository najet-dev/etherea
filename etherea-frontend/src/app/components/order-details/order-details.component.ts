import { Component, inject, effect, DestroyRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AppFacade } from 'src/app/services/appFacade.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommandItem } from '../models/commandItem.model';

@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css'],
  standalone: false,
})
export class OrderDetailsComponent {
  commandItems: CommandItem[] = [];

  private appFacade = inject(AppFacade);
  private route = inject(ActivatedRoute);

  constructor() {
    const destroyRef = inject(DestroyRef);

    const orderId = +this.route.snapshot.paramMap.get('id')!;
    this.appFacade
      .getOrderId(orderId)
      .pipe(takeUntilDestroyed(destroyRef))
      .subscribe({
        next: (data) => {
          this.commandItems = data;
        },
      });
  }
}

import { Component, Input } from '@angular/core';
import { Cart } from '../models/cart.model';
import { ProductTypeService } from 'src/app/services/product-type.service';

@Component({
  selector: 'app-product-display',
  templateUrl: './product-display.component.html',
  styleUrls: ['./product-display.component.css'],
})
export class ProductDisplayComponent {
  @Input() item!: Cart;
  @Input() showVolume: boolean = true;

  constructor(public productTypeService: ProductTypeService) {}
}

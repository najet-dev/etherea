import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreamRoutingModule } from './cream-routing.module';
import { Product } from '../models';
import { SearchService } from 'src/app/services/search.service';

@NgModule({
  declarations: [],
  imports: [CommonModule, CreamRoutingModule],
})
export class CreamModule {}

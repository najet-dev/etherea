import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DayCreamRoutingModule } from './day-cream-routing.module';
import { NormalSkinComponent } from './normal-skin/normal-skin.component';
import { CombinationSkinComponent } from './combination-skin/combination-skin.component';
import { SensitiveSkinComponent } from './sensitive-skin/sensitive-skin.component';
import { MatureSkinComponent } from './mature-skin/mature-skin.component';
import { DrySkinComponent } from './dry-skin/dry-skin.component';
import { BlemishedSkinComponent } from './blemished-skin/blemished-skin.component';

@NgModule({
  declarations: [
    NormalSkinComponent,
    CombinationSkinComponent,
    SensitiveSkinComponent,
    MatureSkinComponent,
    DrySkinComponent,
    BlemishedSkinComponent,
  ],
  imports: [CommonModule, DayCreamRoutingModule],
})
export class DayCreamModule {}

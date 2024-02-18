import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NormalSkinComponent } from './normal-skin/normal-skin.component';
import { CombinationSkinComponent } from './combination-skin/combination-skin.component';
import { SensitiveSkinComponent } from './sensitive-skin/sensitive-skin.component';
import { MatureSkinComponent } from './mature-skin/mature-skin.component';
import { DrySkinComponent } from './dry-skin/dry-skin.component';
import { BlemishedSkinComponent } from './blemished-skin/blemished-skin.component';

const routes: Routes = [
  { path: 'normal-skin', component: NormalSkinComponent },
  { path: 'combination-skin', component: CombinationSkinComponent },
  { path: 'sensitive-skin', component: SensitiveSkinComponent },
  { path: 'mature-skin', component: MatureSkinComponent },
  { path: 'dry-skin', component: DrySkinComponent },
  { path: 'blemished-skin', component: BlemishedSkinComponent },
  // Ajoutez d'autres routes si nécessaire
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DayCreamRoutingModule {}

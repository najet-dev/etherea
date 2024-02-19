import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HairComponent } from './hair.component';

const routes: Routes = [{ path: '', component: HairComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HairRoutingModule {}

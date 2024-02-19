import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreamComponent } from './cream.component';

const routes: Routes = [{ path: '', component: CreamComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CreamRoutingModule {}

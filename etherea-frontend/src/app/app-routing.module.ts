import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { DayCreamComponent } from './components/day-cream/day-cream.component';
import { NightCreamComponent } from './components/night-cream/night-cream.component';

const routes: Routes = [
  { path: '', component: HomeComponent },

  {
    path: 'day-cream',
    loadChildren: () =>
      import('./components/day-cream/day-cream.module').then(
        (m) => m.DayCreamModule
      ),
  },
  {
    path: 'night-cream',
    loadChildren: () =>
      import('./components/night-cream/night-cream.module').then(
        (m) => m.NightCreamModule
      ),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';

const routes: Routes = [
  { path: '', component: HomeComponent },

  {
    path: 'cream',
    loadChildren: () =>
      import('./components/cream/cream.module').then((m) => m.CreamModule),
  },
  {
    path: 'hair',
    loadChildren: () =>
      import('./components/hair/hair.module').then((m) => m.HairModule),
  },
  {
    path: 'new',
    loadChildren: () =>
      import('./components/new/new.module').then((m) => m.NewModule),
  },
  {
    path: 'contact',
    loadChildren: () =>
      import('./components/contact/contact.module').then(
        (m) => m.ContactModule
      ),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}

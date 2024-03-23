import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { ProductDetailsComponent } from './components/productDetails/productDetails.component';
import { CartComponent } from './components/cart/cart.component';
import { SignupComponent } from './components/signup/signup.component';
import { SigninComponent } from './components/signin/signin.component';
import { authGuard } from './components/helpers/auth.guard';

import { AccesComponent } from './components/acces/access.component';
import { ProfileComponent } from './components/profile/profile.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },

  { path: 'productDetails/:id', component: ProductDetailsComponent },
  {
    path: 'cart',
    component: CartComponent,
  },

  { path: 'signup', component: SignupComponent },
  { path: 'signin', component: SigninComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'forbidden', component: AccesComponent },
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
  {
    path: 'admin',
    loadChildren: () =>
      import('./components/admin/admin.module').then((m) => m.AdminModule),
    canActivate: [authGuard],
    data: { roles: ['admin'] },
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}

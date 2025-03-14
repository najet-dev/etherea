import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { ProductDetailsComponent } from './components/productDetails/productDetails.component';
import { CartComponent } from './components/cart/cart.component';
import { AccesComponent } from './components/acces/access.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SignupComponent } from './components/signup/signup.component';
import { SigninComponent } from './components/signin/signin.component';
import { FavoriteComponent } from './components/favorite/favorite.component';
import { OrderComponent } from './components/order/order.component';
import { DeliveryMethodComponent } from './components/delivery-method/delivery-method.component';
import { EmailComponent } from './components/email/email.component';
import { PasswordComponent } from './components/password/password.component';
import { LegalInformationComponent } from './components/legal-information/legal-information.component';
import { CookiesComponent } from './components/cookies/cookies.component';
import { PersonalDataCharterComponent } from './components/personal-data-charter/personal-data-charter.component';
import { SaleConditionComponent } from './components/sale-condition/sale-condition.component';
import { FaqComponent } from './components/faq/faq.component';
import { StoryComponent } from './components/story/story.component';

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
  { path: 'profile', component: ProfileComponent },
  { path: 'forbidden', component: AccesComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'signin', component: SigninComponent },
  { path: 'favorites', component: FavoriteComponent },
  { path: 'order', component: OrderComponent },
  { path: 'order/:addressId', component: OrderComponent },
  { path: 'deliveryMethod/:addressId', component: DeliveryMethodComponent },
  { path: 'email', component: EmailComponent },
  { path: 'password', component: PasswordComponent },
  { path: 'legal-information', component: LegalInformationComponent },
  { path: 'politique-cookies', component: CookiesComponent },
  { path: 'personal-Data-Charter', component: PersonalDataCharterComponent },
  { path: 'sale-condition', component: SaleConditionComponent },
  { path: 'faq', component: FaqComponent },
  { path: 'story', component: StoryComponent },

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
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}

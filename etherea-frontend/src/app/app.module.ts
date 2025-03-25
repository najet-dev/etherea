import { CUSTOM_ELEMENTS_SCHEMA, NgModule, LOCALE_ID } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';

registerLocaleData(localeFr, 'fr');

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HomeComponent } from './components/home/home.component';
import { MenuComponent } from './components/menu/menu.component';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { MatListModule } from '@angular/material/list';
import { CreamComponent } from './components/cream/cream.component';
import { NewComponent } from './components/new/new.component';
import { HairComponent } from './components/hair/hair.component';
import { ProductDetailsComponent } from './components/productDetails/productDetails.component';
import { CartComponent } from './components/cart/cart.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AccesComponent } from './components/acces/access.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SignupComponent } from './components/signup/signup.component';
import { SigninComponent } from './components/signin/signin.component';
import { ProductSummaryComponent } from './components/product-summary/product-summary.component';
import { AuthInterceptor } from './components/helpers/authInterceptor';
import { MatPaginatorModule } from '@angular/material/paginator';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { FavoriteComponent } from './components/favorite/favorite.component';
import { AppFacade } from 'src/app/services/appFacade.service';

import { FavoriteService } from './services/favorite.service';
import { OrderComponent } from './components/order/order.component';
import { DeliveryMethodComponent } from './components/delivery-method/delivery-method.component';
import { ProductDisplayComponent } from './components/product-display/product-display.component';
import { NgxStripeModule } from 'ngx-stripe';
import { environment } from '../environments/environment';
import { CommonModule } from '@angular/common';
import { PaymentComponent } from './components/payment/payment.component';
import { FooterComponent } from './components/footer/footer.component';
import { LegalInformationComponent } from './components/legal-information/legal-information.component';
import { CookiesComponent } from './components/cookies/cookies.component';
import { CookiePopupComponent } from './components/cookie-popup/cookie-popup.component';
import { CookieService } from 'ngx-cookie-service';
import { PersonalDataCharterComponent } from './components/personal-data-charter/personal-data-charter.component';
import { SaleConditionComponent } from './components/sale-condition/sale-condition.component';
import { FaqComponent } from './components/faq/faq.component';
import { StoryComponent } from './components/story/story.component';
import { UpdateEmailComponent } from './components/update-email/update-email.component';
import { UpdatePasswordComponent } from './components/update-password/update-password.component';
import { ContactComponent } from './components/contact/contact.component';
import { BrandComponent } from './components/brand/brand.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { PurchasesComponent } from './components/purchases/purchases.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    MenuComponent,
    CreamComponent,
    NewComponent,
    HairComponent,
    ContactComponent,
    ProductDetailsComponent,
    CartComponent,
    AccesComponent,
    ProfileComponent,
    SignupComponent,
    SigninComponent,
    ProductSummaryComponent,
    SidebarComponent,
    FavoriteComponent,
    OrderComponent,
    DeliveryMethodComponent,
    ProductDisplayComponent,
    PaymentComponent,
    UpdateEmailComponent,
    UpdatePasswordComponent,
    FooterComponent,
    LegalInformationComponent,
    CookiesComponent,
    CookiePopupComponent,
    PersonalDataCharterComponent,
    SaleConditionComponent,
    FaqComponent,
    StoryComponent,
    UpdatePasswordComponent,
    BrandComponent,
    ResetPasswordComponent,
    ForgotPasswordComponent,
    PurchasesComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    NgxStripeModule.forRoot(environment.stripePublicKey),
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: LOCALE_ID, useValue: 'fr' },
    FavoriteService,
    AppFacade,
    CookieService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}

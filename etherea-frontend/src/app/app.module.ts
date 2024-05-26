import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
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
import { ContactComponent } from './components/contact/contact.component';
import { ProductDetailsComponent } from './components/productDetails/productDetails.component';
import { CartComponent } from './components/cart/cart.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AdminComponent } from './components/admin/admin.component';
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
    AdminComponent,
    AccesComponent,
    ProfileComponent,
    SignupComponent,
    SigninComponent,
    ProductSummaryComponent,
    SidebarComponent,
    FavoriteComponent,
    OrderComponent,
  ],
  imports: [
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
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    FavoriteService,
    AppFacade,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}

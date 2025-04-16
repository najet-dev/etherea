import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminMenuComponent } from './admin-menu/admin-menu.component';
import { AdminDashbordComponent } from './admin-dashbord/admin-dashbord.component';
import { UserListComponent } from './user-list/user-list.component';
import { AddUserComponent } from './add-user/add-user.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { ProductListComponent } from './product-list/product-list.component';
import { AddProductComponent } from './add-product/add-product.component';
import { UpdateProductComponent } from './update-product/update-product.component';
import { AdminGuard } from 'src/app/guards/admin.guard';
import { AdminComponent } from './admin.component';
import { VolumeListComponent } from './volume-list/volume-list.component';
import { UpdateVolumeComponent } from './update-volume/update-volume.component';
import { AddVolumeComponent } from './add-volume/add-volume.component';
import { OrderListComponent } from './order-list/order-list.component';
import { TipListComponent } from './tip-list/tip-list.component';
import { UpdateTipComponent } from './update-tip/update-tip.component';
import { AddTipComponent } from './add-tip/add-tip.component';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { NewsletterAdminComponent } from './newsletter-admin/newsletter-admin.component';

@NgModule({
  declarations: [
    AdminComponent,
    AdminMenuComponent,
    AdminDashbordComponent,
    UserListComponent,
    AddUserComponent,
    ProductListComponent,
    AddProductComponent,
    UpdateProductComponent,
    VolumeListComponent,
    UpdateVolumeComponent,
    AddVolumeComponent,
    OrderListComponent,
    TipListComponent,
    AdminDashbordComponent,
    AdminMenuComponent,
    UpdateTipComponent,
    AddTipComponent,
    NewsletterAdminComponent,
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    FormsModule,
    MatDialogModule,
    MatPaginatorModule,
    MatIconModule,
    MatInputModule,
    RouterModule,
    ReactiveFormsModule,
  ],
})
export class AdminModule {}

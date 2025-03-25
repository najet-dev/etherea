import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminComponent } from './admin.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { UserListComponent } from './user-list/user-list.component';
import { ProductListComponent } from './product-list/product-list.component';
import { AdminMenuComponent } from './admin-menu/admin-menu.component';
import { AddProductComponent } from './add-product/add-product.component';
import { FormsModule } from '@angular/forms';
import { UpdateProductComponent } from './update-product/update-product.component';
import { VolumeListComponent } from './volume-list/volume-list.component';
import { AddVolumeComponent } from './add-volume/add-volume.component';
import { UpdateVolumeComponent } from './update-volume/update-volume.component';

@NgModule({
  declarations: [
    AdminComponent,
    AdminDashboardComponent,
    UserListComponent,
    ProductListComponent,
    AdminMenuComponent,
    AddProductComponent,
    UpdateProductComponent,
    VolumeListComponent,
    AddVolumeComponent,
    UpdateVolumeComponent,
  ],
  imports: [CommonModule, AdminRoutingModule, FormsModule],
})
export class AdminModule {}

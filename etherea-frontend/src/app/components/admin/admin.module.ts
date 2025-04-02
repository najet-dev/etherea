import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminComponent } from './admin.component';
import { UserListComponent } from './user-list/user-list.component';
import { ProductListComponent } from './product-list/product-list.component';
import { AdminMenuComponent } from './admin-menu/admin-menu.component';
import { AddProductComponent } from './add-product/add-product.component';
import { FormsModule } from '@angular/forms';
import { UpdateProductComponent } from './update-product/update-product.component';
import { VolumeListComponent } from './volume-list/volume-list.component';
import { AddVolumeComponent } from './add-volume/add-volume.component';
import { UpdateVolumeComponent } from './update-volume/update-volume.component';
import { AddUserComponent } from './add-user/add-user.component';
import { OrderListComponent } from './order-list/order-list.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { TipListComponent } from './tip-list/tip-list.component'; // Ajout du module

@NgModule({
  declarations: [
    AdminComponent,
    UserListComponent,
    ProductListComponent,
    AdminMenuComponent,
    AddProductComponent,
    UpdateProductComponent,
    VolumeListComponent,
    AddVolumeComponent,
    UpdateVolumeComponent,
    AddUserComponent,
    OrderListComponent,
    TipListComponent,
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    FormsModule,
    MatDialogModule,
    MatPaginatorModule,
    MatIconModule,
  ],
})
export class AdminModule {}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin.component';
import { UserListComponent } from './user-list/user-list.component';
import { ProductListComponent } from './product-list/product-list.component';
import { AddProductComponent } from './add-product/add-product.component';
import { UpdateProductComponent } from './update-product/update-product.component';
import { VolumeListComponent } from './volume-list/volume-list.component';
import { AddVolumeComponent } from './add-volume/add-volume.component';
import { UpdateVolumeComponent } from './update-volume/update-volume.component';
import { AddUserComponent } from './add-user/add-user.component';
import { OrderListComponent } from './order-list/order-list.component';
import { TipListComponent } from './tip-list/tip-list.component';

const routes: Routes = [
  {
    path: '',
    component: AdminComponent,
    children: [
      {
        path: 'admin-users',
        component: UserListComponent,
      },
      { path: 'add-user', component: AddUserComponent },

      {
        path: 'admin-products',
        component: ProductListComponent,
      },
      { path: 'add-product', component: AddProductComponent },
      { path: 'update-product', component: UpdateProductComponent },
      { path: 'admin-volumes', component: VolumeListComponent },
      { path: 'add-volume', component: AddVolumeComponent },
      { path: 'update-volume', component: UpdateVolumeComponent },
      { path: 'admin-orders', component: OrderListComponent },
      { path: 'admin-tips', component: TipListComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}

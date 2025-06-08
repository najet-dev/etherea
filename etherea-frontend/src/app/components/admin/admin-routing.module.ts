import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin.component';
import { UserListComponent } from './user-list/user-list.component';
import { ProductListComponent } from './product-list/product-list.component';
import { AddProductComponent } from './add-product/add-product.component';
import { UpdateProductComponent } from './update-product/update-product.component';
import { AddUserComponent } from './add-user/add-user.component';
import { AdminDashbordComponent } from './admin-dashbord/admin-dashbord.component';
import { VolumeListComponent } from './volume-list/volume-list.component';
import { AddVolumeComponent } from './add-volume/add-volume.component';
import { UpdateVolumeComponent } from './update-volume/update-volume.component';
import { OrderListComponent } from './order-list/order-list.component';
import { TipListComponent } from './tip-list/tip-list.component';
import { AddTipComponent } from './add-tip/add-tip.component';
import { UpdateTipComponent } from './update-tip/update-tip.component';
import { AdminGuard } from 'src/app/guards/admin.guard';
import { NewsletterAdminComponent } from './newsletter-admin/newsletter-admin.component';

const routes: Routes = [
  {
    path: '',
    component: AdminComponent,
    canActivate: [AdminGuard],
    children: [
      {
        path: '',
        redirectTo: 'admin-dashboard',
        pathMatch: 'full',
      },
      {
        path: 'admin-dashboard',
        component: AdminDashbordComponent,
      },
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
      { path: 'add-tip', component: AddTipComponent },
      { path: 'update-tip', component: UpdateTipComponent },
      {
        path: 'admin-news-letter',
        component: NewsletterAdminComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}

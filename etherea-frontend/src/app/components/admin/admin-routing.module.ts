import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { UserListComponent } from './user-list/user-list.component';
import { ProductListComponent } from './product-list/product-list.component';
import { AddProductComponent } from './add-product/add-product.component';
import { UpdateProductComponent } from './update-product/update-product.component';

const routes: Routes = [
  {
    path: '',
    component: AdminComponent,
    children: [
      {
        path: '',
        redirectTo: 'admin-dashboard',
        pathMatch: 'full',
      },
      {
        path: 'admin-dashboard',
        component: AdminDashboardComponent,
      },
      {
        path: 'admin-users',
        component: UserListComponent,
      },
      {
        path: 'admin-products',
        component: ProductListComponent,
      },
      { path: 'add-product', component: AddProductComponent },
      { path: 'update-product', component: UpdateProductComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}

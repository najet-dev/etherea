import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { NightBlemishedComponent } from './night-blemished/night-blemished.component';
import { NightCombinationComponent } from './night-combination/night-combination.component';
import { NightDryComponent } from './night-dry/night-dry.component';
import { NightMatureComponent } from './night-mature/night-mature.component';
import { NightNormalComponent } from './night-normal/night-normal.component';
import { NightSensitiveComponent } from './night-sensitive/night-sensitive.component';

const routes: Routes = [
  { path: 'night-normal', component: NightNormalComponent },
  { path: 'night-combination', component: NightCombinationComponent },
  { path: 'night-sensitive', component: NightSensitiveComponent },
  { path: 'night-mature', component: NightMatureComponent },
  { path: 'night-dry', component: NightDryComponent },
  { path: 'night-blemished', component: NightBlemishedComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NightCreamRoutingModule {}

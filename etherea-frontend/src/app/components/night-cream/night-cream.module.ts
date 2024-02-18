import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NightCreamRoutingModule } from './night-cream-routing';

import { NightBlemishedComponent } from './night-blemished/night-blemished.component';
import { NightCombinationComponent } from './night-combination/night-combination.component';
import { NightDryComponent } from './night-dry/night-dry.component';
import { NightMatureComponent } from './night-mature/night-mature.component';
import { NightNormalComponent } from './night-normal/night-normal.component';
import { NightSensitiveComponent } from './night-sensitive/night-sensitive.component';

@NgModule({
  declarations: [
    NightBlemishedComponent,
    NightCombinationComponent,
    NightDryComponent,
    NightMatureComponent,
    NightNormalComponent,
    NightSensitiveComponent,
  ],

  imports: [CommonModule, NightCreamRoutingModule],
})
export class NightCreamModule {}

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { NptComponent } from './npt.component';

const routes: Routes = [
  {
    path: '',
    component: NptComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NptRoutingModule { }

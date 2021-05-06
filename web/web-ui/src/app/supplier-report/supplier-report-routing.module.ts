import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SupplierReportComponent } from './supplier-report.component';

const routes: Routes = [
  {
    path: '',
    component: SupplierReportComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SupplierReportRoutingModule { }

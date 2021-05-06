import { ContractDetailComponent } from './contract-detail/contract-detail.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CustomPreloadStrategyService } from './shared/services/custom-preload-strategy.service';
import { InvoicesComponent } from './invoices/invoices.component';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './shared/components/login/login.component';
import { Page404Component } from './shared/components/page404/page404.component';
import { CountComponent } from './count/count.component';


const routes: Routes = [
  {
    path: 'supplier-report',
    // canActivate: [AuthGuard],
    loadChildren: './supplier-report/supplier-report.module#SupplierReportModule',
    data: { preload: false }
  },
  {
    path: 'invoice-detail',
    // canActivate: [AuthGuard],
    loadChildren: './invoice-detail/invoice-detail.module#InvoiceDetailModule',
    data: { preload: true }
  },
  {
    path: 'contract-detail',
    // canActivate: [AuthGuard],
    loadChildren: './contract-detail/contract-detail.module#ContractDetailModule',
    data: { preload: true }
  },
  {
    path: 'npt',
    // canLoad: [AuthGuard],
    loadChildren: './npt/npt.module#NptModule',
    data: { preload: false }
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'invoices',
    // canActivate: [AuthGuard],
    component: InvoicesComponent
  },
  {
	    path: 'getinvoicecount',
	    // canActivate: [AuthGuard],
	    component: CountComponent
	  },
  {
    path: '',
    redirectTo: '/invoices',
    pathMatch: 'full'
  },
  {
    path: '**',
    component: Page404Component
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    preloadingStrategy: CustomPreloadStrategyService,
    useHash: true
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }

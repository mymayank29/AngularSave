import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FiltersComponent } from './components/filters/filters.component';
import { MaterialModule } from './material/material.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { LoaderComponent } from './components/loader/loader.component';
import { Page404Component } from './components/page404/page404.component';
import { HeaderComponent } from './components/header/header.component';
import { BannnerCommingComponent } from './components/bannner-comming/bannner-comming.component';
import { BreadcrumbsComponent } from './components/breadcrumbs/breadcrumbs.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { SearchComponent } from './components/search/search.component';
import { ClickOutsideModule } from 'ng-click-outside';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    ClickOutsideModule
  ],
  exports: [
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    FiltersComponent,
    RouterModule,
    HeaderComponent,
    Page404Component,
    LoaderComponent,
    LoginComponent,
    BannnerCommingComponent,
    BreadcrumbsComponent,
    NavigationComponent,
    SearchComponent,
    ClickOutsideModule
  ],
  declarations: [
    FiltersComponent,
    HeaderComponent,
    Page404Component,
    LoaderComponent,
    LoginComponent,
    BannnerCommingComponent,
    BreadcrumbsComponent,
    NavigationComponent,
    SearchComponent]
})
export class SharedModule { }

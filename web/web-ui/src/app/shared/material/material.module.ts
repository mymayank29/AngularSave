import { NgModule } from '@angular/core';

import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTableModule, MatButtonModule, MatSliderModule, MatMenuModule, MatNativeDateModule, MatRadioModule } from '@angular/material';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDividerModule } from '@angular/material/divider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatDatepickerModule} from '@angular/material/datepicker';
import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material';
import { ClickStopDirective } from '../directives/click-stop.directive';
import { OnlyDigitDirective } from '../directives/only-digit.directive';
import { LinkStyleDirective } from '../directives/link-style.directive';

const modules = [
  MatButtonModule,
  MatCardModule,
  MatToolbarModule,
  MatTableModule,
  MatSortModule,
  MatPaginatorModule,
  MatSelectModule,
  MatInputModule,
  MatFormFieldModule,
  MatDividerModule,
  MatSlideToggleModule,
  MatTooltipModule,
  MatSnackBarModule,
  MatIconModule,
  MatAutocompleteModule,
  MatSliderModule,
  MatProgressBarModule,
  MatMenuModule,
  MatTabsModule,
  MatRadioModule,
  MatCheckboxModule,
  MatDatepickerModule,
  MatNativeDateModule
];

@NgModule({
  imports: modules,
  declarations: [
    ClickStopDirective,
    LinkStyleDirective,
    OnlyDigitDirective
  ],
  exports: [
    modules,
    ClickStopDirective,
    LinkStyleDirective,
    OnlyDigitDirective
  ]
})
export class MaterialModule {
  constructor(
    iconRegistry?: MatIconRegistry,
    sanitizer?: DomSanitizer) {
      iconRegistry
        .addSvgIcon('input', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/svg/ic_input_24px.svg'))
        .addSvgIcon('person', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/svg/ic_person_24px.svg'))
        .addSvgIcon('expand', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/svg/baseline-fullscreen-24px.svg'))
        .addSvgIcon('unexpand', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/svg/baseline-fullscreen_exit-24px.svg'))
        .addSvgIcon('back', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/svg/baseline-arrow_back_ios-24px.svg'))
        .addSvgIcon('nav', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/svg/baseline-menu-24px.svg'))
        .addSvgIcon('search', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/svg/baseline-search-24px.svg'))
        .addSvgIcon('close', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/svg/ic_close_18px.svg'));
    }
}

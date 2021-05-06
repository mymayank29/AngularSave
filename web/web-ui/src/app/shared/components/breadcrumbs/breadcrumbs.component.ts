import { TogglesState } from './../../models/toggles-interface';
import { Filters } from './../../models/filter-interface';
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-breadcrumbs',
  templateUrl: './breadcrumbs.component.html',
  styleUrls: ['./breadcrumbs.component.css']
})
export class BreadcrumbsComponent {
  @Input() filtersState: Filters;
  @Input() togglesState: TogglesState;

  @Output() resetFilters: EventEmitter<any> = new EventEmitter<any>();

  public onResetFilters() {
    this.resetFilters.emit();
  }

}

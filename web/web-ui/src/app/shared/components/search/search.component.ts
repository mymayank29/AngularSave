import { Component, Output, EventEmitter, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { Observable, Subscription, fromEvent } from 'rxjs';
import { AppState, getFiltersState, FiltersState } from 'src/app/+store';
import { isNullOrUndefined } from 'util';
import { debounceTime, switchMap, map, startWith, distinctUntilChanged } from 'rxjs/operators'; 
import { FormControl } from '@angular/forms';
@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})

export class SearchComponent implements OnInit{
  @Output() onSearching: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild('searchInput') 
  input: ElementRef;

  private subscription: Subscription;

  public value = '';
   // changed here
  private subFiltersState: Subscription;
   // changed here
  constructor(private store: Store<AppState>) {
  }
   // changed here
  ngOnInit() {
      this.subFiltersState = this.store.pipe(select(getFiltersState))
                            .subscribe((filterState: FiltersState) => {
                              this.value = filterState.filtersState['searching'];
                            });
    this.search(this.value, 'searching');
  }

  ngAfterViewInit(): void {
    const terms$ = fromEvent<any>(this.input.nativeElement, 'keyup')
         .pipe(
           map(event => event.target.value),
          // startWith(''),
           debounceTime(1000),
           distinctUntilChanged()
         );
      this.subscription = terms$
         .subscribe(
           criterion => {
             this.search(criterion, 'searching');
           }
         );
   }

  search(value, name) {
    const searchData = {value, name};
    this.onSearching.emit(searchData);
  }

}

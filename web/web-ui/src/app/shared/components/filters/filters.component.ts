import { ChangeDetectionStrategy, Component, Output, EventEmitter, OnInit, Input, OnDestroy, ChangeDetectorRef, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatSlideToggleChange, MatSliderChange } from '@angular/material';

import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

import { FilterState } from '../../models/filter-state-interface';
import { Store, select } from '@ngrx/store';
import { AppState, getFiltersData } from '../../../+store';
import * as FiltersActions from '../../../+store/filters/filters.actions';
import { Filters } from '../../models/filter-interface';
import { TogglesState } from '../../models/toggles-interface';
import { Router } from '@angular/router';
import { isNullOrUndefined } from 'util';
import { DatePipe } from '@angular/common';
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import { DurationTextService } from './duration.service';
@Component({
  selector: 'app-filters',
  templateUrl: './filters.component.html',
  styleUrls: ['./filters.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FiltersComponent implements OnInit, OnDestroy, OnChanges {

  public categoryManagerControl: FormControl = new FormControl();
  public wellControl: FormControl = new FormControl();
  public supplierControl: FormControl = new FormControl();
  public dateFromControl: FormControl = new FormControl();
  public dateToControl: FormControl = new FormControl();
  public statusControl: FormControl = new FormControl();

  public filterCategoryManager$: Observable<string[]>;
  public filterWell$: Observable<string[]>;
  public filterSupplier$: Observable<string[]>;
  public filterFromDate$: Observable<string[]>;
  public filterToDate$: Observable<string[]>;
  public filterSatus$: Observable<string[]>;

  
  public wellname_subscribe: string[];
  public category_manager_subscribe: string[];
  public parent_vendor_subscribe: string[];
  public submit_date_subscribe: string[];
  public date_second_subscribe: string[];
  public status_subscribe: string[];




  public filtersSub: Subscription;
  selectedNPTType= 'N';
  public showNPTDurationText = true;

  public isWeather: 'Weather' | 'Non Weather' | 'Both' = 'Non Weather';
  public isMatchedByDate: 'Yes' | 'No' = 'Yes';
  public isMatchedByContract: 'Yes' | 'No' = 'No';

  private wellname: string[];
  private category_manager: string[];
  private parent_vendor: string[];
  private submit_date: string[];
  private date_second: string[];
  private status: string[];

  public nptStartValue: number = 0;
  public nptFinishValue: number = 1000;
  public openMatCard: boolean = false;
  public fromButton: number = 0;
  public messageMinVal: string = '';
  public messageMaxVal: string  = '';
  public validMinValue = true;
  public validMaxValue = true;
  public inValidMinValue = false;
  public inValidMaxValue = false;
  @Input() filterTheme: string;
  @Input() togglesState: TogglesState;
  @Input() maxNptDuration: number;

  @Output() clearSingleFilter: EventEmitter<string> = new EventEmitter<string>();
  @Output() onToggleNPT: EventEmitter<string> = new EventEmitter<string>();
  @Output() onChangeNptDuration: EventEmitter<any> = new EventEmitter<any>();
  @Output() onToggleMatchByDate: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() onToggleMatchByContract: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() onDispatchFiltersState: EventEmitter<string> = new EventEmitter<string>();
  @Output() resetFilters: EventEmitter<string> = new EventEmitter<string>();

  @ViewChild('fromDate') fromDate;
  @ViewChild('toDate') toDate;
  
  constructor(private store: Store<AppState>,
    private cdRef: ChangeDetectorRef,
    private router: Router,
    private datePipe: DatePipe,
    public durationText: DurationTextService) {}

  ngOnInit() {
    this.filtersSub = this.store.pipe(select(getFiltersData))
      .subscribe(
        (filters: Filters) => {
          this.category_manager = [...filters.contract_owner_name];
          this.wellname = [...filters.wellname];
          this.parent_vendor = [...filters.parent_vendor];
          this.date_second = [...filters.date_second];
          this.submit_date = [...filters.submit_date];
          this.status = [...filters.status];
          setTimeout(() => {
            this.setFiltersOptions();
          });
        });
        setTimeout(() => {
         this.defineNptStartValue();
        });
  }

  ngOnChanges(changes: SimpleChanges) {
     this.setMaxNpt();
  }

  ngOnDestroy() {
    this.filtersSub.unsubscribe();
    this.resetNpts();
  }
  onResetFilters() {
    this.resetFilters.emit();
    
  }
 
  private defineNptStartValue() {
     if ((JSON.parse(sessionStorage.getItem('nptStartValue'))) && this.router.url !== '/supplier-report') {
        this.nptStartValue = JSON.parse(sessionStorage.getItem('nptStartValue'));
      } else {
        this.nptStartValue = 0;
      }
  }

  private setNptStartToSS(value) {
    sessionStorage.setItem('nptStartValue', JSON.stringify(value));
  }

  private setMaxNpt() {
    if (this.nptFinishValue <= 0) {
      this.nptFinishValue = this.maxNptDuration;
    }
    this.cdRef.detectChanges();
  }

  public setFinishNpt() {
      setTimeout(()=>{
        this.nptFinishValue = this.maxNptDuration;
      });
  }

  public publishFilter(value: string, name: string): void {
    if(name === 'submit_date' || name === 'date_second') {
      value = this.datePipe.transform(value, 'yyyy-MM-dd');
      value = value.toString();
    }
    if(value.indexOf(undefined)!== -1) {
      this.resetFilter(name);
      return;
    }
    if ((name === 'status' || 'contract_owner_name' || 'wellname') && (value.length === 0)) {
      value = '';
    }
    const filterObj: Filters = {[name]: [value]};
    this.store.dispatch(new FiltersActions.UpdateFilters(filterObj));
    this.onDispatchFiltersState.emit();
    this.setFiltersOptions();
    this.setMaxNpt();
    this.setFinishNpt();
  }


  public resetFilter(name: string): void {
    if(name == 'submit_date') {
      let htmlElement = <HTMLElement>this.fromDate.nativeElement;
      setTimeout(() => {
        htmlElement.blur();
      });
    }
    if(name == 'date_second') {
      let htmlElement = <HTMLElement>this.toDate.nativeElement;
      setTimeout(() => {
        htmlElement.blur();
      });
    }

    this.clearSingleFilter.emit(name);
    this.setFinishNpt();
  }

  public toggleNPT(event: MatSlideToggleChange): void {
    event.checked ? this.isWeather = 'Weather' : this.isWeather = 'Non Weather';
    this.onToggleNPT.emit(event.checked ? 'Y' : 'N');
  }

  public toggleNPTNew(evt): void{
    switch (evt) {
      case 'Y':
        this.isWeather = 'Weather';
        break;
      case 'B':
        this.isWeather = 'Both';
        break;
      case 'N':
        this.isWeather = 'Non Weather';
        break;
      default:
        this.isWeather = 'Non Weather';
    }
    this.onToggleNPT.emit(evt);
    
  }

  private createNptDurationData(lowNpt, highNpt) {
    return {
      nptdurationlow: lowNpt,
      nptdurationhigh: highNpt
    };
  }
/**
 *  public validMinValue = true;
  public validMaxValue = true;
  public inValidMinValue = false;
  public inValidMaxValue = false;
 */
  public changeNptDuration(): void {
    if (isNullOrUndefined(this.nptStartValue) && isNullOrUndefined(this.nptFinishValue)) {
      this.messageMinVal = 'Please enter a valid value';
      this.messageMaxVal = 'Please enter a valid value';
      this.validMinValue = false;
      this.validMaxValue = false;
      this.inValidMinValue = true;
      this.inValidMaxValue = true;
      return;
    }
    if (isNullOrUndefined(this.nptStartValue)) {
      this.messageMinVal = 'Please enter a valid value';
      this.validMinValue = false;
      this.inValidMinValue = true;
      
      if (this.nptFinishValue > 10000) {
        this.messageMaxVal = 'Value should be less than 10000';
        this.validMaxValue = false;
        this.inValidMaxValue = true; 
      }
      else {
        this.validMaxValue = true;
        this.inValidMaxValue = false;
      }
      return;
    }
    if (isNullOrUndefined(this.nptFinishValue)) {
      this.messageMaxVal = 'Please enter a valid value';
      this.validMaxValue = false;
      this.inValidMaxValue = true;
      
      if(this.nptStartValue < 0) {
        this.messageMinVal = 'Value is less than zero';
        this.validMinValue = false;
        this.inValidMinValue = true;
      }
      else {
      this.validMinValue = true;
      this.inValidMinValue = false;
      }
      return;
    }

    if(this.nptStartValue < 0 && this.nptFinishValue > 10000) {
      this.messageMinVal = 'Value is less than zero';
      this.messageMaxVal = 'Value should be less than 10000';
      this.validMinValue = false;
      this.inValidMinValue = true;
      this.validMaxValue = false;
      this.inValidMaxValue = true;
      return;
    }
    
    if (this.nptStartValue < 0) {
      this.messageMinVal = 'Value is less than zero';
      this.validMinValue = false;
      this.inValidMinValue = true;
      this.validMaxValue = true;
      this.inValidMaxValue = false;
      return;
    }
    if (this.nptFinishValue > 10000) {
      this.messageMaxVal = 'Value should be less than 10000';
      this.validMaxValue = false;
      this.inValidMaxValue = true;
      this.validMinValue = true;
      this.inValidMinValue = false;
      return;
    }
    this.validMinValue = true;
    this.validMaxValue = true;
    this.inValidMaxValue = false;
    this.inValidMinValue = false;
    if (this.nptStartValue > this.nptFinishValue) {
      this.nptStartValue = this.nptFinishValue;
    } else if (this.nptStartValue < 0) {
      this.nptStartValue = 0;
    } else if (this.nptFinishValue < this.nptStartValue) {
      this.nptFinishValue = this.nptStartValue;
    }
    this.setNptStartToSS(this.nptStartValue);
    const nptdurationData = this.createNptDurationData(this.nptStartValue, this.nptFinishValue);
    this.onChangeNptDuration.emit(nptdurationData);
    this.durationText.showNPTDurationText = false;
  }

  public toggleByDate(event: MatSlideToggleChange): void {
    event.checked ? this.isMatchedByDate = 'Yes' : this.isMatchedByDate = 'No';
    this.onToggleMatchByDate.emit(event.checked);
  }

  public toggleByContract(event: MatSlideToggleChange): void {
    event.checked ? this.isMatchedByContract = 'Yes' : this.isMatchedByContract = 'No';
    this.onToggleMatchByContract.emit(event.checked);
  }

  public setControls(filtersState: FilterState): void {
    this.categoryManagerControl.setValue(filtersState.contract_owner_name);
    this.wellControl.setValue(filtersState.wellname);
    this.supplierControl.setValue(filtersState.parent_vendor);

    
    if(filtersState.submit_date == ''){
      this.dateFromControl.setValue(filtersState.submit_date);
    }
    else {
      this.dateFromControl.setValue(new Date(filtersState.submit_date+"T00:00:00"));
    }
    if(filtersState.date_second == ''){
      this.dateToControl.setValue(filtersState.date_second);
    }
    else {
      this.dateToControl.setValue(new Date(filtersState.date_second+"T00:00:00"));
    }
    
    this.statusControl.setValue(filtersState.status);
  }

  private filterArr(arr: string[], value: any): string[] {
    if (arr.length !== 0) {
      return arr.filter(el => el.includes(value));
    }
  }

  private initFiltersOptions(control: FormControl, array: string[]): Observable<string[]> {
      return control.valueChanges.pipe(
        startWith(''),
        map(value => this.filterArr(array, value))
      );
  }

  // private setCrossOptionsForFilter(filterObservable$: Observable<string[]>, filterArraySubscribe: string[]) {
  //      filterObservable$.subscribe((res) => {
  //     if  (!isNullOrUndefined(res) && !(res.length === 0)) {
  //       filterArraySubscribe = [...res];
  //       console.log(filterArraySubscribe);
  //     }
  //     else {
  //       if (isNullOrUndefined(res)) {
  //         filterArraySubscribe = [];
  //       }
  //       else {
  //         filterArraySubscribe =  filterArraySubscribe ?  filterArraySubscribe : [];
  //       }
  //     }
  //     return filterArraySubscribe;
  //    });
  // }

  private setFiltersOptions(): void {
    this.filterCategoryManager$ = this.initFiltersOptions(this.categoryManagerControl, this.category_manager);
    this.filterWell$ = this.initFiltersOptions(this.wellControl, this.wellname);
    this.filterSupplier$ = this.initFiltersOptions(this.supplierControl, this.parent_vendor);
    this.filterFromDate$ = this.initFiltersOptions(this.dateFromControl, this.submit_date);
    this.filterToDate$ = this.initFiltersOptions(this.dateToControl, this.date_second);
    this.filterSatus$ = this.initFiltersOptions(this.statusControl, this.status);
    // this.category_manager_subscribe =  this.setCrossOptionsForFilter(this.filterCategoryManager$, this.category_manager_subscribe);
    // this.filterCategoryManager$.subscribe((categoryManagers) => {
    //                                   if(!isNullOrUndefined(categoryManagers) && !(categoryManagers.length === 0)) {
    //                                     this.category_manager_subscribe = [...categoryManagers];
    //                                   }
    //                                   else {
    //                                     if (isNullOrUndefined(categoryManagers)) {
    //                                       this.category_manager_subscribe = [];
    //                                     }
    //                                     else {
    //                                     this.category_manager_subscribe =  this.category_manager_subscribe ?  this.category_manager_subscribe : [];
    //                                     }
    //                                   }
    //                                  });
    // this.filterWell$.subscribe((wells) => {
    //                                   if(!isNullOrUndefined(wells) && !(wells.length === 0)) {
    //                                     this.wellname_subscribe = [...wells];
    //                                   }
    //                                   else {
    //                                     if (isNullOrUndefined(wells)) {
    //                                       this.wellname_subscribe = [];
    //                                     }
    //                                     else {
    //                                     this.wellname_subscribe = this.wellname_subscribe ? this.wellname_subscribe :[];
    //                                     }
    //                                   }
    //                                   });
    // this.filterSupplier$.subscribe((suppliers) => {
    //                                   if(!isNullOrUndefined(suppliers) && !(suppliers.length === 0)) {
    //                                     this.parent_vendor_subscribe = [...suppliers];
    //                                   }
    //                                   else {
    //                                     if (isNullOrUndefined(suppliers)) {
    //                                       this.parent_vendor_subscribe = [];
    //                                     }
    //                                     else {
    //                                     this.parent_vendor_subscribe = this.parent_vendor_subscribe ? this.parent_vendor_subscribe :[];
    //                                     }
    //                                   }
    //                                   });
    // this.filterFromDate$.subscribe((fromDate) => {
    //                                 if(!isNullOrUndefined(fromDate) && !(fromDate.length === 0)) {
    //                                   this.submit_date_subscribe = [...fromDate];
    //                                 }
    //                                 else {
    //                                   if (isNullOrUndefined(fromDate)) {
    //                                     this.submit_date_subscribe = [];
    //                                   }
    //                                   else {
    //                                   this.submit_date_subscribe = this.submit_date_subscribe ? this.submit_date_subscribe :[];
    //                                   }
    //                                 }
    //                                 });
    // this.filterToDate$.subscribe((toDate) => {
    //                               if(!isNullOrUndefined(toDate) && !(toDate.length === 0)) {
    //                                 this.date_second_subscribe = [...toDate];
    //                               }
    //                               else {
    //                                 if (isNullOrUndefined(toDate)) {
    //                                   this.date_second_subscribe = [];
    //                                 }
    //                                 else {
    //                                 this.date_second_subscribe = this.date_second_subscribe ? this.date_second_subscribe : [];
    //                                 }
    //                               }
    //                               });
    // this.filterSatus$.subscribe((status) => {
    //                             if(!isNullOrUndefined(status) && !(status.length === 0)) {
    //                               this.status_subscribe = [...status];
    //                             }
    //                             else {
    //                               if (isNullOrUndefined(status)) {
    //                                 this.status_subscribe = [];
    //                               }
    //                               else {
    //                               this.status_subscribe = this.status_subscribe ? this.status_subscribe :[];
    //                               }
    //                             }
    //                             });

                                      

  }

   resetNptDuration() {
    this.validMinValue = true;
    this.validMaxValue = true;
    this.inValidMaxValue = false;
    this.inValidMinValue = false;
    this.nptStartValue = 0;
   // this.nptFinishValue = this.maxNptDuration;
    this.setNptStartToSS(this.nptStartValue);
    const nptdurationData = this.createNptDurationData(this.nptStartValue, 1000);
    this.onChangeNptDuration.emit(nptdurationData);
    this.durationText.showNPTDurationText = true;
  }

  resetNpts() {
    // this.nptFinishValue = 0;
    // const nptdurationData = this.createNptDurationData(this.nptStartValue, 1000);
    const nptdurationData = this.createNptDurationData(this.nptStartValue, this.nptFinishValue);
    this.onChangeNptDuration.emit(nptdurationData);
  }

  resetToDefaultNPTs() {
    const nptdurationData = this.createNptDurationData(0, 1000);
    this.onChangeNptDuration.emit(nptdurationData);
  }

  nptDuration() {
      this.openMatCard = !this.openMatCard;
      this.fromButton = 1;
  }
  onClickedOutside() {
    if(this.fromButton === 0) {
      this.openMatCard = !this.openMatCard;
    }
    this.fromButton = 0;
  }
}

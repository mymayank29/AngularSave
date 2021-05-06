import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingLineItems {
  public  selectedloadingLineItems: Subject<boolean> = new BehaviorSubject<boolean>(null);
 //  public selectedloadingLineItems$ = this.selectedloadingLineItems.asObservable();

  publishLoadingLineItems(loading: boolean) {
    this.selectedloadingLineItems.next(loading);
  }

}
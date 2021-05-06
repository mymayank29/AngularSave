import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingNptEvent {
  public  selectedloadingNptEvent: Subject<boolean> = new BehaviorSubject<boolean>(null);
 //  public selectedloadingLineItems$ = this.selectedloadingLineItems.asObservable();

  publishLoadingNptEvents(loading: boolean) {
    this.selectedloadingNptEvent.next(loading);
  }

}
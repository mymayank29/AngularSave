import { Observable, of } from 'rxjs';
import { Injectable } from '@angular/core';
import { Action } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';
import * as FlaggedInvoicesAction from './flagged-invoices.actions';
import {SupplierReportService} from '../../supplier-report/share/services/supplier-report.service';
import { FlaggedInvoicesActions, GetFlaggedInvoices } from './flagged-invoices.actions';
import { concatMap, pluck, switchMap, map, catchError } from 'rxjs/operators';

@Injectable()
export class FlaggedInvoicesEffects {
  // $action - Observable from all apps actions
  constructor(private actions$: Actions,
              private supplierReportService: SupplierReportService) {}

  @Effect()
  getFlaggedInvoices$: Observable<Action> = this.actions$.pipe(
    ofType<FlaggedInvoicesAction.GetFlaggedInvoices>(FlaggedInvoicesAction.FlaggedInvoicesActionTypes.GET_FLAGGED_INVOICES),
    pluck('payload'),
    switchMap(payload => this.supplierReportService.getFlaggedInvoices(payload[0], payload[1], payload[2], payload[3], payload[4])
      .pipe(
        map(flaggedInvoices => new FlaggedInvoicesAction.GetFlaggedInvoicesSuccess(flaggedInvoices),
        catchError(err => of(new FlaggedInvoicesAction.GetFlaggedInvoicesError(err)))
        )
      )
    )
  );
}

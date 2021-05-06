import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';
import * as InvoicesAction from './invoices.actions';

import { Observable, of } from 'rxjs';
import { concatMap, pluck, switchMap, map, catchError } from 'rxjs/operators';

import { InvoiceItem } from '../../shared/models/invoice-item-model';
import { InvoiceService } from '../../invoices/shared/services/invoice.service';
import { from } from 'rxjs';

@Injectable()
export class InvoicesEffects {

  // $action - Observable from all apps actions
  constructor(private actions$: Actions,
              private invoiceService: InvoiceService) {}

  @Effect()
  getInvoices$: Observable<Action> = this.actions$.pipe(
    ofType<InvoicesAction.GetInvoices>(InvoicesAction.InvoicesActionTypes.GET_INVOICES),
    pluck('payload'),
    switchMap(payload => from(this.invoiceService.getAllInvoicesParamatized(payload[0], payload[1], payload[2], payload[3], payload[4]))
        .pipe(
          map(invoices => new InvoicesAction.GetInvoicesSuccess(invoices),
          catchError(err => of(new InvoicesAction.GetInvoicesError(err)))
        )
      )
    )
  );

  @Effect()
  updateInvoice$: Observable<Action> = this.actions$.pipe(
    ofType<InvoicesAction.UpdateInvoice>(InvoicesAction.InvoicesActionTypes.UPDATE_INVOICE),
    pluck('payload'),
    concatMap((payload: InvoiceItem[]) => this.invoiceService.updateInvoice(payload)
    .pipe(
        map(invoice => new InvoicesAction.UpdateInvoiceSuccess(invoice)),
        catchError(err => of(new InvoicesAction.UpdateInvoiceError(err)))
      )
    )
  );
}

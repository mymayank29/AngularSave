import { FlaggedInvoiceItem } from '../../../shared/models/flagged-invoice-item';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { UrlHelperService } from '../../../shared/services/url-helper.service';
import { FlaggedInvoice } from '../../../shared/models/flagged-invoice-model';

@Injectable({
  providedIn: 'root'
})
export class SupplierReportService {

  private url: string;

  constructor(
    private http: HttpClient,
    private urlHelperService: UrlHelperService) {
      this.url = urlHelperService.getUrl('flaggedInvoicesBaseUrl');
  }

  getFlaggedInvoices(isWether: string, isMatchByDate: string, isMatchByContract: string, nptdurationlow: number, nptdurationhigh: number): Observable<FlaggedInvoiceItem[]> {
    const params = new HttpParams()
            .set('isweather', isWether.toString())
            .set('ismatchbydate', isMatchByDate.toString())
            .set('ismatchbycontract', isMatchByContract.toString())
            .set('nptdurationlow', nptdurationlow.toString())
            .set('nptdurationhigh', nptdurationhigh.toString());

    return this.http.get<FlaggedInvoiceItem[]>(this.url, { params })
      .pipe(
        map(res => res['payload']),
        catchError(this.handleError)
      );
  }
  private handleError(err: HttpErrorResponse) {
    let errorMessage: string;

    if (err.error instanceof Error) {
      errorMessage = `An error occured: ${err.error.message}`;
    } else {
      errorMessage = `Backend returned code ${err.status}, body was: ${err.error}`;
    }

    console.error(errorMessage);
    return throwError(errorMessage);
  }
  updateflaggedInvoices(flaggedInvoices: FlaggedInvoice[]): Observable<FlaggedInvoice[]> {
    const url = `${this.url}/out-of-scope`;
    const body = JSON.stringify(flaggedInvoices);
    const options = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };

    let params = new HttpParams()
    .set('flaggedInvoices', body.toString());

    return this.http.get<FlaggedInvoice[]>(url, { params });


    // return this.http
    //   .put<FlaggedInvoice[]>(url, body, options)
    //   .pipe(catchError(this.handleError));
  }
  updateInvoicesStatus(invoicesIds) {
    const url = `${this.url}/supplier-review`;
    const body = JSON.stringify(invoicesIds);
    const options = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    return this.http.put(url, body, options);
  }
}

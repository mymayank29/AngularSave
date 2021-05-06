import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { LineItem } from '../../../shared/models/line-item-model';
import { UrlHelperService } from '../../../shared/services/url-helper.service';

@Injectable({
  providedIn: 'root'
})
export class LineItemsService {

  private url: string;

  constructor(
    private http: HttpClient,
    private urlHelperService: UrlHelperService) {
      this.url = urlHelperService.getUrl('lineItemsBaseUrl');
  }

  getLetItemsByAribaId(aribaIds: string[]): Observable<LineItem[]> {
    return this.http
      .get<LineItem[]>(`${this.url}/${aribaIds}`)
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

    // console.error(errorMessage);
    return throwError(errorMessage);
  }

}

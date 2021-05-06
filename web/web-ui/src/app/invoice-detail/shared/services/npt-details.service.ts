import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { NPTDetail } from '../../../shared/models/npt-detail-model';
import { UrlHelperService } from '../../../shared/services/url-helper.service';

@Injectable({
  providedIn: 'root'
})
export class NptDetailsService {

  private url: string;

  constructor(
    private http: HttpClient,
    private urlHelperService: UrlHelperService) {
      this.url = urlHelperService.getUrl('nptsBaseUrl');
  }

  getNPTsByAribaId(aribaIds: string[], isWeather: string, isMatchByDate: boolean): Observable<NPTDetail[]> {
    let params = new HttpParams()
      .set('isMatchByDate', isMatchByDate.toString())
      .set('isWeather', isWeather);

    return this.http
      .get<NPTDetail[]>(`${this.url}/${aribaIds}`, { params })
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

}

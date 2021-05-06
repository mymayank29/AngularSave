import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, forkJoin, throwError, of } from 'rxjs';
import { catchError, map, timeout, mergeMap, concatMap } from 'rxjs/operators';
import { InvoiceItem } from '../../../shared/models/invoice-item-model';
import { UrlHelperService } from '../../../shared/services/url-helper.service';
import { isNullOrUndefined } from 'util';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  public url: string;

  constructor(private http: HttpClient,
              private urlHelperService: UrlHelperService) {
    this.url = urlHelperService.getUrl('invoicesBaseUrl');
  }
  
  getAllInvoicesParamatized(isWeather: String, isMatchByDate: boolean, isMatchByContract?: boolean, nptdurationlow?: any, nptdurationhigh?: any): Observable<InvoiceItem[]> {

	  let params = new HttpParams()
	            .set('isweather', isWeather.toString())
	            .set('ismatchbydate', isMatchByDate.toString())
	            .set('ismatchbycontract', isMatchByContract.toString())
	            .set('nptdurationlow', nptdurationlow.toString())
	            .set('nptdurationhigh', nptdurationhigh.toString())
	            .set('pageno', '')
	            .set('pagesize', '');

	 const invoiceCountUrl = `${this.url}/getinvoicecount`;
	    
	 return this.http.get<String>(invoiceCountUrl, { params })
			.pipe(concatMap((recordcount: Response) => {
				return forkJoin(this.getApiCallList(params, Number(300).toString())) //recordcount['count']
				//return forkJoin(this.getApiCallList(params, Number(300).toString()))
					 .pipe(map(responses => {
						 return [].concat(...responses);
				 }));
			}));
	   
  }
  
  getInvoiceById(invoiceId: string): Observable<InvoiceItem> {
    return this.http.get<InvoiceItem>(`${this.url}/${invoiceId}`);
  }

  updateInvoice(invoices: InvoiceItem[]): Observable<any> {
    

    //const url = `${this.url}/update`;
    
    //const body = JSON.stringify(invoices);
    // const options = {
    //   headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    // };
    // const invoicesIds = [];
    // invoices.map(invoice => {
    //   invoicesIds.push(invoice.id);
	// });
	// console.log("here");
	// console.log(invoicesIds);
    // let params = new HttpParams()
    // .set('ids', JSON.stringify(invoicesIds))
    // .set('invoices', body.toString());

//	return this.http.get<String>(url, { params });

	return forkJoin(this.getUpdateApiCallList(invoices));
  }

  private handleError(err: HttpErrorResponse) {
    let errorMessage: string;

    if (err.error instanceof Error) {
      errorMessage = `An error occured: ${err.error.message}`;
    } else {
      errorMessage = `Backend returned code ${err.status}, body was: ${err.error}`;
    }
    return throwError(errorMessage);
  }
  
  getRecordCount(params: HttpParams):Observable<any>{
	 const invoiceCountUrl = `${this.url}/getinvoicecount`;
	  
	 return this.http.get<String>(invoiceCountUrl, { params })
		.pipe(map((resp:Response) =>JSON.stringify(resp['count'])));
  }
  
   private getApiCallList(params: HttpParams, recordCt: string){
	  
	  let callArray = []
	  
	  console.log('url ==>'+this.url);
	  const reccount = (Number(recordCt) > 0) ? Number(recordCt) : 1;
	  
	  const pgsize = this.getPageSize(reccount);
	  const numApiCalls = this.getApiCallCount(pgsize, reccount);
	 
	  let params2 = new HttpParams({
		  fromString: params.toString()
	  });
	  
	  for(var i=0;i<numApiCalls;i++){
			  
		  let params = new HttpParams();
		  params = params.set('isweather', params2.get('isweather'));
		  params = params.set('ismatchbydate', params2.get('ismatchbydate'));
		  params = params.set('ismatchbycontract', params2.get('ismatchbycontract'));
		  params = params.set('nptdurationlow', params2.get('nptdurationlow'));
		  params = params.set('nptdurationhigh', params2.get('nptdurationhigh'));
          params = params.set('pageno', i.toString());
		  params = params.set('pagesize', pgsize.toString());
		  
		  callArray.push(this.http.get<InvoiceItem[]>(this.url, { params })
				  				.pipe(map(res => res['payload']),catchError(this.handleError))); 
		  
	  }
	  
	  return callArray;
  }

  private getUpdateApiCallList(invoices: InvoiceItem[]){

    let callArray = [];
    const arraySliceIncrement = 100;
	const url = `${this.url}/update`;
	  
	let subArrayCt = invoices.length > 100 ?  Math.floor(invoices.length/arraySliceIncrement) : 0;
	let lastCallSliceIncrement = (invoices.length % arraySliceIncrement);
	let lastCallUpperBound = (subArrayCt * arraySliceIncrement) + lastCallSliceIncrement;
	let lastCallLowerBound = (subArrayCt * arraySliceIncrement) + 1;
	  
		for(var i=0;i<=subArrayCt;i++){
		//add api calls to array

		//let lBound = i>0 ? (i * arraySliceIncrement)+1 : i;
		//let uBound = lBound - 1 + arraySliceIncrement;

		let lBound = i*arraySliceIncrement;
		let uBound = lBound + arraySliceIncrement;

		let invoicesSubArray = [];

		console.log('<==before slice==>');
		  
		invoicesSubArray = invoices.slice(lBound,uBound);
		
		console.log('uBound==>'+uBound);
		console.log('lBound==>'+lBound);
		console.log('invoicesSubArray==>'+invoicesSubArray.toString());
		  
		if(isNullOrUndefined(invoicesSubArray) || invoicesSubArray.length == 0) {
				break;
		}
		  
		let invoicesIds = [];
			invoicesSubArray.map(invoice => {
			invoicesIds.push(invoice.id);
		});	

		let body = JSON.stringify(invoicesSubArray);
		let params = new HttpParams()
			.set('ids', JSON.stringify(invoicesIds))
			.set('invoices', body.toString());

		callArray.push(this.http.get<String>(url, { params }));

	}
	  
	return callArray;
  }

  private getParams(invoicesSubArray: InvoiceItem[]) {

	  const body = JSON.stringify(invoicesSubArray);

	  const invoicesIds = [];
			invoicesSubArray.map(invoice => {
			invoicesIds.push(invoice.id);
	  });

	  let updateParams = new HttpParams()
			.set('ids', JSON.stringify(invoicesIds))
			.set('invoices', body.toString());
	  
	  return updateParams;

  }
  
  private getPageSize(recordct: number){
//	  return (recordct > 50000) ? 500: 1000;
	  return 1000;
  }
  
  private getApiCallCount(pagesize: number, recct: number){
	  return (recct/pagesize) + 1; 
  }

}

import { Injectable } from '@angular/core';

import { delay, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthServiceMock {
  isLoggedIn = false;

  // store the URL so we can redirect after logging in
  redirectUrl: string;

  login(): Observable<boolean> {
    // emitation - after 1 sec return isLoggedIn = true
    return of(true).pipe(
      delay(1000),
      tap(val => (this.isLoggedIn = val))
    );
  }

  logout(): void {
    this.isLoggedIn = false;
  }
}

import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UrlHelperService {

  private window: Window = window;
  private environment = environment;
  private origin: string;

  public getUrl(endPoint: string) {
    if (!this.window.location.origin) {
      this.origin =
      this.window.location.protocol +
        '//' +
        this.window.location.hostname +
        (this.window.location.port ? ':' + this.window.location.port : '');
    } else {
      const location = this.window.location.origin;
      this.origin =
        location.indexOf('localhost:4200') !== -1
          ? 'http://localhost:13080'
          : location;
    }
   // return this.origin + this.environment[endPoint];
    return "https://gomsave-dev.chevron.com";
  }
}

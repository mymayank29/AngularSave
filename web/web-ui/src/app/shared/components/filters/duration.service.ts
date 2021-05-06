import { Duration } from './duration.model';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
  })
  export class DurationTextService {
      public showNPTDurationText = true;
  }
import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[click.stop]'
})
export class ClickStopDirective {

  @HostListener('click', ['$event'])
  onClick(event) {
    event.stopPropagation();
  }

}

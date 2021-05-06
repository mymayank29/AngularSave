import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[onlydigit]'
})
export class OnlyDigitDirective {

  @HostListener('keydown', ['$event'])
  onKeyDown(event) {
    const e = <KeyboardEvent>event;

    if (['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'Enter', 'Backspace', 'Delete', '.', '$'].indexOf(e.key) !== -1 ||
      // Allow: Ctrl+A
      (e.keyCode == 65 && e.ctrlKey === true) ||
      // Allow: Ctrl+C
      (e.keyCode == 67 && e.ctrlKey === true) ||
      // Allow: Ctrl+X
      (e.keyCode == 88 && e.ctrlKey === true) ||
      // Allow: home, end, left, right
      (e.keyCode >= 35 && e.keyCode <= 39)) {
      return;
    } else {
      e.preventDefault();
    }

  }

}

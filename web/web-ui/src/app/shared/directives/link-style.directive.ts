import { Directive, HostBinding } from '@angular/core';
import { style } from '@angular/animations';

@Directive({
  selector: '[appLinkStyle]'
})
export class LinkStyleDirective {
  @HostBinding('style.cursor')
  cursor: string = 'pointer';
  @HostBinding('style.color')
  color: string = '#0b2d71';
  @HostBinding('style.text-decoration')
  textDecoration: string = 'underline';
  @HostBinding('style.font-weight')
  fontWeight: string = 'bold';
}

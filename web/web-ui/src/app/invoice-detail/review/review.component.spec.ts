import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewComponent } from './review.component';
import { SharedModule } from '../../shared/shared.module';
import { AppRoutingModule } from '../../app-routing.module';
import { InvoicesModule } from '../../invoices/invoices.module';
import { MainStoreModule } from '../../+store/main-store.module';
import { HttpClientModule } from '@angular/common/http';

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule,
        InvoicesModule,
        MainStoreModule,
        HttpClientModule
      ],
      declarations: [ ReviewComponent ]
    })
    .compileComponents();
  }));

  it('should create', () => {
    fixture = TestBed.createComponent(ReviewComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});

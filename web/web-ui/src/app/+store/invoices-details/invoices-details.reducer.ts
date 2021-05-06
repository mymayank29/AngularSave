import { InvoicesDetailsActionTypes, InvoicesDetailsActions } from './invoices-details.actions';
import { initialInvoicesDetailsState, InvoicesDetailsState } from './invoices-details.state';
import { InvoiceItem } from '../../shared/models/invoice-item-model';

export function invoicesDetailsReducer(state: InvoicesDetailsState = initialInvoicesDetailsState, action: InvoicesDetailsActions): InvoicesDetailsState {
  // case must be return new state(object that build from copy of old state and changes in this object)
  switch (action.type) {
    case InvoicesDetailsActionTypes.LOAD_INVOICES_DETAILS: {
      return {
        ...state
       };
      }
    case InvoicesDetailsActionTypes.SET_INVOICES_DETAILS: {
      const data = [ ...(<InvoiceItem[]>action.payload) ];
      return {
        ...state,
        data,
        loading: false,
        loaded: true
       };
    }
    default: {
      return state;
    }
  }
}

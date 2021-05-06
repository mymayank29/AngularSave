import { FlaggedInvoicesState, initialFlaggedInvoicesState } from './flagged-invoices.state';
import { FlaggedInvoicesActions, FlaggedInvoicesActionTypes } from './flagged-invoices.actions';
import { FlaggedInvoiceItem } from 'src/app/shared/models/flagged-invoice-item';

export function flaggedInvoicesReducer(state: FlaggedInvoicesState = initialFlaggedInvoicesState, action: FlaggedInvoicesActions): FlaggedInvoicesState {
    // case must be return new state(object that build from copy of old state and changes in this object)
  switch (action.type) {
    case FlaggedInvoicesActionTypes.GET_FLAGGED_INVOICES: {
      return {
        ...state,
        loading: true
      };
    }

    case FlaggedInvoicesActionTypes.GET_FLAGGED_INVOICES_SUCCESS: {
      const data = [...(<FlaggedInvoiceItem[]>action.payload)];
      return {
        ...state,
        data,
        loading: false,
        loaded: true
      };
    }

    case FlaggedInvoicesActionTypes.GET_FLAGGED_INVOICES_ERROR: {
      const error = action.payload;
      return {
        ...state,
        loading: false,
        loaded: false,
        error
      };
    }

    default: {
      return state;
    }
  }
}

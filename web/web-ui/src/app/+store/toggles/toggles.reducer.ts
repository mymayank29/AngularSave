import { initialTogglesState } from './toggles.state';
import { TogglesActions, TogglesActionTypes } from './toggles.actions';
import { TogglesState } from '../../shared/models/toggles-interface';
export function togglesReducer(state = initialTogglesState, action: TogglesActions): TogglesState {
  switch (action.type) {
    case TogglesActionTypes.LOAD_TOGGLES: {
      return {
        ...state
      };
    }
    case TogglesActionTypes.UPDATE_TOGGLES_STATE: {
      const updatedState = {...state, ...action.payload};
      return updatedState;
    }
    default:
      return state;
  }
}

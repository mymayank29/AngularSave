import { GetFilterValue } from './../filters/filters.actions';
import { Action } from '@ngrx/store';
import { TogglesState } from '../../shared/models/toggles-interface';

export enum TogglesActionTypes {
  LOAD_TOGGLES = '[Toggles] LOAD_TOGGLES',
  UPDATE_TOGGLES_STATE = '[Toggles] UPDATE_TOGGLES_STATE',
  SET_DEFAULT_TOGGLES_STATE = '[Toggles] SET_DEFAULT_TOGGLES_STATE',
  SET_TOGGLES_DATA = '[Toggles] SET_TOGGLES_DATA',
}


export class LoadToggles implements Action {
  readonly type = TogglesActionTypes.LOAD_TOGGLES;
}
export class UpdateToggles implements Action {
  readonly type = TogglesActionTypes.UPDATE_TOGGLES_STATE;
  constructor(public payload: any) {}
}

export class SetStartingTogglesState implements Action {
  readonly type = TogglesActionTypes.SET_DEFAULT_TOGGLES_STATE;
}

export type TogglesActions =
  | LoadToggles
  | UpdateToggles
  | GetFilterValue;

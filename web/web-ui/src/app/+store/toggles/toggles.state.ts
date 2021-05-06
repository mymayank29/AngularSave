import { TogglesState } from '../../shared/models/toggles-interface';
import { FiltersState } from '../filters/filters.state';


const defaultTogglesState: TogglesState = {
  isweather: 'N',
  nptdurationlow: 0,
  nptdurationhigh: 10000,
  ismatchbydate: true,
  ismatchbycontract: false
};

export const initialTogglesState = defaultTogglesState;

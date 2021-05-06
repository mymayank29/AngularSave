import { createFeatureSelector } from '@ngrx/store';
import { TogglesState } from '../../shared/models/toggles-interface';

export const getTogglesState = createFeatureSelector<TogglesState>('toggles');

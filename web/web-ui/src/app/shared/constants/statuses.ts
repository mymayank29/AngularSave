import { Status } from '../models/status-interface';

export const STATUSES: Status[] = [
    { value: 'Not Reviewed', viewValue: 'Not Reviewed' },
    { value: 'Flagged for Review', viewValue: 'Flagged for Review' },
    { value: 'Supplier Review', viewValue: 'Supplier Review' },
    { value: 'SCM Recovered/Closed', viewValue: 'SCM Recovered/Closed' },
    { value: 'NPT Out of Scope', viewValue: 'NPT Out of Scope' }
];

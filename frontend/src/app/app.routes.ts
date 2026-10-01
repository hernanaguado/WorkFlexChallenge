import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/workation/workation-table/workation-table').then((m) => m.WorkationTable),
  },
  { path: '**', redirectTo: '' },
];
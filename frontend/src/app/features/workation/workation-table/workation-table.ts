import { LiveAnnouncer } from '@angular/cdk/a11y';
import { DatePipe } from '@angular/common';
import { Component, DestroyRef, effect, inject, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { RiskLevel, Workation } from '../workation.model';
import { WorkationService } from '../workation.service';

type LoadStatus = 'loading' | 'loaded' | 'error';

interface RiskDisplay {
  label: string;
  icon: string;
  severity: number;
}

/** LOW and NO are both labelled "No risk" (different colors), as required by the specification. */
const RISK_DISPLAY: Record<RiskLevel, RiskDisplay> = {
  HIGH: { label: 'High risk', icon: 'icons/red-risk.svg', severity: 3 },
  LOW: { label: 'No risk', icon: 'icons/yellow-risk.svg', severity: 2 },
  NO: { label: 'No risk', icon: 'icons/green-risk.svg', severity: 1 },
};

@Component({
  selector: 'app-workation-table',
  imports: [DatePipe, MatTableModule, MatSortModule, MatProgressSpinnerModule],
  templateUrl: './workation-table.html',
  styleUrl: './workation-table.scss',
})
export class WorkationTable {
  private readonly workationService = inject(WorkationService);
  private readonly liveAnnouncer = inject(LiveAnnouncer);
  private readonly destroyRef = inject(DestroyRef);
  private readonly sort = viewChild(MatSort);

  protected readonly displayedColumns = [
    'employee',
    'origin',
    'destination',
    'start',
    'end',
    'workingDays',
    'risk',
  ];
  protected readonly dataSource = new MatTableDataSource<Workation>([]);
  protected readonly status = signal<LoadStatus>('loading');

  constructor() {
    this.dataSource.sortingDataAccessor = (workation, column) =>
      column === 'risk'
        ? RISK_DISPLAY[workation.risk].severity
        : workation[column as keyof Workation] ?? '';

    // The table (and its MatSort) only exists once data is loaded, so bind the sort reactively.
    effect(() => {
      const sort = this.sort();
      if (sort) {
        this.dataSource.sort = sort;
      }
    });

    this.loadWorkations();
  }

  protected loadWorkations(): void {
    this.status.set('loading');
    this.workationService
      .getAll()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (workations) => {
          this.dataSource.data = workations;
          this.status.set('loaded');
        },
        error: () => this.status.set('error'),
      });
  }

  protected riskDisplay(risk: RiskLevel): RiskDisplay {
    return RISK_DISPLAY[risk];
  }

  protected announceSortChange(sortState: Sort): void {
    if (sortState.direction) {
      this.liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    } else {
      this.liveAnnouncer.announce('Sorting cleared');
    }
  }
}
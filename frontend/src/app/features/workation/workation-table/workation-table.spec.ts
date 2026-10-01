import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { W1, W2, W3, W4, W5, WORKATIONS } from '../workation.fixtures';
import { Workation } from '../workation.model';
import { WorkationService } from '../workation.service';
import { WorkationTable } from './workation-table';

const RED_ICON = 'icons/red-risk.svg';
const YELLOW_ICON = 'icons/yellow-risk.svg';
const GREEN_ICON = 'icons/green-risk.svg';
const HIGH_RISK_LABEL = 'High risk';
const NO_RISK_LABEL = 'No risk';
const UNKNOWN_COUNTRY = 'Atlantis';
const EMPTY_MESSAGE = 'No workations found.';
const ERROR_MESSAGE = 'Workations could not be loaded.';
const W1_START_DISPLAY = '02/01/2024';
const W1_END_DISPLAY = '31/12/2024';

const COLUMN = {
  employee: 'employee',
  origin: 'origin',
  destination: 'destination',
  start: 'start',
  end: 'end',
  risk: 'risk',
} as const;

const flagUrl = (code: string | null): string => `https://flagcdn.com/w40/${code}.png`;

describe('WorkationTable', () => {
  let fixture: ComponentFixture<WorkationTable>;

  async function render(response: Observable<Workation[]>): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [WorkationTable],
      providers: [{ provide: WorkationService, useValue: { getAll: () => response } }],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkationTable);
    await fixture.whenStable();
  }

  function element(): HTMLElement {
    return fixture.nativeElement as HTMLElement;
  }

  function rows(): HTMLTableRowElement[] {
    return Array.from(element().querySelectorAll<HTMLTableRowElement>('tr.mat-mdc-row'));
  }

  function columnTexts(column: string): string[] {
    return rows().map((row) => row.querySelector(`.mat-column-${column}`)?.textContent?.trim() ?? '');
  }

  function riskIcons(): string[] {
    return rows().map((row) => row.querySelector('.risk-icon')?.getAttribute('src') ?? '');
  }

  async function clickHeader(column: string): Promise<void> {
    element().querySelector<HTMLElement>(`th.mat-column-${column}`)?.click();
    fixture.detectChanges();
    await fixture.whenStable();
  }

  it('should render one row per workation', async () => {
    // when
    await render(of(WORKATIONS));

    // then
    expect(rows().length).toBe(WORKATIONS.length);
    expect(columnTexts(COLUMN.employee)).toContain(W5.employee);
  });

  it('should format dates as dd/MM/yyyy', async () => {
    // when
    await render(of([W1]));

    // then
    expect(columnTexts(COLUMN.start)).toEqual([W1_START_DISPLAY]);
    expect(columnTexts(COLUMN.end)).toEqual([W1_END_DISPLAY]);
  });

  it('should show flag and country name, hiding the flag when there is no country code', async () => {
    // when
    await render(of([{ ...W1, destinationCode: null }]));

    // then
    const [row] = rows();
    expect(row.querySelector('.mat-column-origin img')?.getAttribute('src')).toBe(flagUrl(W1.originCode));
    expect(row.querySelector('.mat-column-destination img')).toBeNull();
    expect(columnTexts(COLUMN.destination)).toEqual([W1.destination]);
  });

  it('should hide both flags but still show the country names when codes are null', async () => {
    // when
    await render(
      of([
        { ...W1, origin: UNKNOWN_COUNTRY, originCode: null, destination: UNKNOWN_COUNTRY, destinationCode: null },
      ]),
    );

    // then
    const [row] = rows();
    expect(row.querySelectorAll('img.flag').length).toBe(0);
    expect(columnTexts(COLUMN.origin)).toEqual([UNKNOWN_COUNTRY]);
    expect(columnTexts(COLUMN.destination)).toEqual([UNKNOWN_COUNTRY]);
  });

  it('should sort a text column ascending, then descending', async () => {
    // given
    await render(of(WORKATIONS));

    // when
    await clickHeader(COLUMN.employee);

    // then
    expect(columnTexts(COLUMN.employee)).toEqual([W4, W5, W3, W1, W2].map((w) => w.employee));

    // when clicked again
    await clickHeader(COLUMN.employee);

    // then
    expect(columnTexts(COLUMN.employee)[0]).toBe(W1.employee);
    expect(columnTexts(COLUMN.employee)[4]).toBe(W4.employee);
  });

  it('should sort dates chronologically rather than by displayed text', async () => {
    // given
    await render(of(WORKATIONS));

    // when
    await clickHeader(COLUMN.start);

    // then
    expect(columnTexts(COLUMN.start)).toEqual([
      '01/09/2022',
      '13/03/2023',
      '23/04/2023',
      '22/05/2023',
      W1_START_DISPLAY,
    ]);
  });

  it('should sort risk by severity (HIGH > LOW > NO)', async () => {
    // given
    await render(of(WORKATIONS));

    // when
    await clickHeader(COLUMN.risk);

    // then
    expect(riskIcons()).toEqual([GREEN_ICON, YELLOW_ICON, RED_ICON, RED_ICON, RED_ICON]);

    // when clicked again
    await clickHeader(COLUMN.risk);

    // then
    expect(riskIcons()).toEqual([RED_ICON, RED_ICON, RED_ICON, YELLOW_ICON, GREEN_ICON]);
  });

  it('should label LOW and NO risk as "No risk" with different icons', async () => {
    // when
    await render(of([W1, W4, W5]));

    // then
    expect(columnTexts(COLUMN.risk)).toEqual([HIGH_RISK_LABEL, NO_RISK_LABEL, NO_RISK_LABEL]);
    expect(riskIcons()).toEqual([RED_ICON, YELLOW_ICON, GREEN_ICON]);
  });

  it('should show an empty state when there are no workations', async () => {
    // when
    await render(of([]));

    // then
    expect(element().querySelector('table')).toBeNull();
    expect(element().textContent).toContain(EMPTY_MESSAGE);
  });

  it('should show an error state when the request fails', async () => {
    // when
    await render(throwError(() => new Error('Network error')));

    // then
    expect(element().querySelector('table')).toBeNull();
    expect(element().querySelector('[role="alert"]')?.textContent).toContain(ERROR_MESSAGE);
  });
});

export type RiskLevel = 'HIGH' | 'LOW' | 'NO';

export interface Workation {
  workationId: string;
  employee: string;
  origin: string;
  originCode: string | null;
  destination: string;
  destinationCode: string | null;
  /** ISO date (yyyy-MM-dd) */
  start: string;
  /** ISO date (yyyy-MM-dd) */
  end: string;
  workingDays: number;
  risk: RiskLevel;
}

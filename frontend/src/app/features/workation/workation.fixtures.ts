import { Workation } from './workation.model';

/** The five workations from workations.csv, as returned by the backend. */

/** HIGH risk, Germany → United States. */
export const W1: Workation = {
  workationId: 'w1',
  employee: 'Steffen Jacobs',
  origin: 'Germany',
  originCode: 'de',
  destination: 'United States',
  destinationCode: 'us',
  start: '2024-01-02',
  end: '2024-12-31',
  workingDays: 65,
  risk: 'HIGH',
};

/** HIGH risk, Germany → Ukraine. */
export const W2: Workation = {
  workationId: 'w2',
  employee: 'Steffen Jacobs',
  origin: 'Germany',
  originCode: 'de',
  destination: 'Ukraine',
  destinationCode: 'ua',
  start: '2023-04-23',
  end: '2023-04-30',
  workingDays: 1,
  risk: 'HIGH',
};

/** HIGH risk, Belgium → Spain. */
export const W3: Workation = {
  workationId: 'w3',
  employee: 'Henry Duchamp',
  origin: 'Belgium',
  originCode: 'be',
  destination: 'Spain',
  destinationCode: 'es',
  start: '2022-09-01',
  end: '2023-03-01',
  workingDays: 131,
  risk: 'HIGH',
};

/** LOW risk, Germany → Greece. */
export const W4: Workation = {
  workationId: 'w4',
  employee: 'Andre Fischer',
  origin: 'Germany',
  originCode: 'de',
  destination: 'Greece',
  destinationCode: 'gr',
  start: '2023-05-22',
  end: '2023-06-30',
  workingDays: 50,
  risk: 'LOW',
};

/** NO risk, Germany → India. */
export const W5: Workation = {
  workationId: 'w5',
  employee: 'Ayushi Singh',
  origin: 'Germany',
  originCode: 'de',
  destination: 'India',
  destinationCode: 'in',
  start: '2023-03-13',
  end: '2023-04-30',
  workingDays: 35,
  risk: 'NO',
};

export const WORKATIONS: Workation[] = [W1, W2, W3, W4, W5];

import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render the WorkFlex logo in the header', async () => {
    // when
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();

    // then
    const logo = (fixture.nativeElement as HTMLElement).querySelector<HTMLImageElement>('header h1 img');
    expect(logo?.getAttribute('src')).toBe('workflex-logo-on-light.svg');
    expect(logo?.alt).toBe('WorkFlex');
  });
});

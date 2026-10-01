import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Workation } from './workation.model';

@Injectable({ providedIn: 'root' })
export class WorkationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/workation`;

  getAll(): Observable<Workation[]> {
    return this.http.get<Workation[]>(this.baseUrl);
  }
}

import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { authConfig } from '../core/auth-config';

@Injectable()
export class ApiService {
  private http = inject(HttpClient);

  getProtectedApiResponse(): Observable<string> {
    return this.http.get<any>(`${authConfig.issuer}/api/v1/users/me`)
      .pipe(
        map(response => 'Response:\n' + JSON.stringify(response)),
        catchError((e: HttpErrorResponse) => of(`🌩 API Error: ${e.status} ${e.statusText}`)),
      );
  }
}

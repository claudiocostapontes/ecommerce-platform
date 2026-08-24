import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ApiResponse<T> {
    success: boolean;
    message?: string;
    data: T;
    timestamp: string;
}

export interface PageResponse<T> {
    content: T[];
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient) {}

    get<T>(endpoint: string, params?: any): Observable<ApiResponse<T>> {
        let httpParams = new HttpParams();
        if (params) {
            Object.keys(params).forEach(key => {
                if (params[key] !== null && params[key] !== undefined) {
                    httpParams = httpParams.set(key, params[key]);
                }
            });
        }
        return this.http.get<ApiResponse<T>>(
            `${this.apiUrl}${endpoint}`,
            { params: httpParams }
        );
    }

    post<T>(endpoint: string, body: any): Observable<ApiResponse<T>> {
        return this.http.post<ApiResponse<T>>(
            `${this.apiUrl}${endpoint}`,
            body
        );
    }

    put<T>(endpoint: string, body: any): Observable<ApiResponse<T>> {
        return this.http.put<ApiResponse<T>>(
            `${this.apiUrl}${endpoint}`,
            body
        );
    }

    delete<T>(endpoint: string): Observable<ApiResponse<T>> {
        return this.http.delete<ApiResponse<T>>(
            `${this.apiUrl}${endpoint}`
        );
    }

    patch<T>(endpoint: string, body: any): Observable<ApiResponse<T>> {
        return this.http.patch<ApiResponse<T>>(
            `${this.apiUrl}${endpoint}`,
            body
        );
    }
}
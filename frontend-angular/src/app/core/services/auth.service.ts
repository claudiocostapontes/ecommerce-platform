import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    user: {
        id: string;
        username: string;
        email: string;
        fullName: string;
        roles: string[];
    };
}

export interface LoginRequest {
    usernameOrEmail: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = `${environment.apiUrl}/auth`;
    private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) {
        this.loadUser();
    }

    login(request: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request)
            .pipe(
                tap(response => {
                    this.setTokens(response);
                    this.currentUserSubject.next(response);
                })
            );
    }

    register(request: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request)
            .pipe(
                tap(response => {
                    this.setTokens(response);
                    this.currentUserSubject.next(response);
                })
            );
    }

    logout(): void {
        this.clearTokens();
        this.currentUserSubject.next(null);
    }

    refreshToken(): Observable<AuthResponse> {
        const refreshToken = this.getRefreshToken();
        return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken })
            .pipe(
                tap(response => {
                    this.setTokens(response);
                    this.currentUserSubject.next(response);
                })
            );
    }

    isLoggedIn(): boolean {
        return !!this.getAccessToken();
    }

    hasRole(role: string): boolean {
        const user = this.currentUserSubject.value;
        return user?.user.roles.includes(role) || false;
    }

    private setTokens(response: AuthResponse): void {
        localStorage.setItem('access_token', response.accessToken);
        localStorage.setItem('refresh_token', response.refreshToken);
        localStorage.setItem('token_expiry', (Date.now() + response.expiresIn).toString());
    }

    private clearTokens(): void {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('token_expiry');
    }

    getAccessToken(): string | null {
        return localStorage.getItem('access_token');
    }

    private getRefreshToken(): string | null {
        return localStorage.getItem('refresh_token');
    }

    private loadUser(): void {
        const token = this.getAccessToken();
        if (token) {
            // In a real app, decode JWT or call user info endpoint
            const userStr = localStorage.getItem('current_user');
            if (userStr) {
                this.currentUserSubject.next(JSON.parse(userStr));
            }
        }
    }
}
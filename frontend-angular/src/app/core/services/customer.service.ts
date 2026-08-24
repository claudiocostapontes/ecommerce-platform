import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService, ApiResponse } from './api.service';

export interface CustomerProfile {
    id: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    cpf?: string;
    birthDate?: string;
    phone?: string;
    addresses: CustomerAddress[];
    newsletterSubscribed: boolean;
    marketingNotifications: boolean;
}

export interface CustomerAddress {
    id: string;
    label: string;
    street: string;
    number: string;
    complement?: string;
    neighborhood: string;
    city: string;
    state: string;
    zipCode: string;
    isDefault: boolean;
    formattedAddress: string;
}

export interface UpdateProfileRequest {
    firstName?: string;
    lastName?: string;
    cpf?: string;
    birthDate?: string;
    phone?: string;
    newsletterSubscribed?: boolean;
    marketingNotifications?: boolean;
}

export interface CreateAddressRequest {
    label: string;
    street: string;
    number: string;
    complement?: string;
    neighborhood: string;
    city: string;
    state: string;
    zipCode: string;
    isDefault?: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class CustomerService {
    constructor(private api: ApiService) {}

    getProfile(): Observable<ApiResponse<CustomerProfile>> {
        return this.api.get('/customer/profile');
    }

    updateProfile(request: UpdateProfileRequest): Observable<ApiResponse<CustomerProfile>> {
        return this.api.put('/customer/profile', request);
    }

    getAddresses(): Observable<ApiResponse<CustomerAddress[]>> {
        return this.api.get('/customer/addresses');
    }

    createAddress(request: CreateAddressRequest): Observable<ApiResponse<CustomerAddress>> {
        return this.api.post('/customer/addresses', request);
    }

    updateAddress(id: string, request: CreateAddressRequest): Observable<ApiResponse<CustomerAddress>> {
        return this.api.put(`/customer/addresses/${id}`, request);
    }

    deleteAddress(id: string): Observable<ApiResponse<void>> {
        return this.api.delete(`/customer/addresses/${id}`);
    }

    addToFavorites(productId: string): Observable<ApiResponse<void>> {
        return this.api.post(`/customer/favorites/${productId}`, {});
    }

    removeFromFavorites(productId: string): Observable<ApiResponse<void>> {
        return this.api.delete(`/customer/favorites/${productId}`);
    }
}
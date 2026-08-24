import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ApiService, ApiResponse } from './api.service';

export interface CartItem {
    id: string;
    productId: string;
    productName: string;
    productSku: string;
    productImage: string;
    quantity: number;
    unitPrice: number;
    subtotal: number;
    available: boolean;
}

export interface Cart {
    id: string;
    items: CartItem[];
    subtotal: number;
    totalItems: number;
}

@Injectable({
    providedIn: 'root'
})
export class CartService {
    private cartSubject = new BehaviorSubject<Cart | null>(null);
    public cart$ = this.cartSubject.asObservable();

    constructor(private api: ApiService) {
        this.loadCart();
    }

    loadCart(): void {
        this.api.get<Cart>('/cart').subscribe(response => {
            if (response.success) {
                this.cartSubject.next(response.data);
            }
        });
    }

    getCart(): Observable<ApiResponse<Cart>> {
        return this.api.get('/cart');
    }

    addItem(productId: string, quantity: number): Observable<ApiResponse<Cart>> {
        return this.api.post('/cart/items', { productId, quantity });
    }

    updateItem(itemId: string, quantity: number): Observable<ApiResponse<Cart>> {
        return this.api.put('/cart/items', { itemId, quantity });
    }

    removeItem(itemId: string): Observable<ApiResponse<Cart>> {
        return this.api.delete(`/cart/items/${itemId}`);
    }

    clearCart(): Observable<ApiResponse<void>> {
        return this.api.delete('/cart');
    }

    getCartValue(): Cart | null {
        return this.cartSubject.value;
    }

    getCartItemsCount(): number {
        return this.cartSubject.value?.totalItems || 0;
    }
}
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService, ApiResponse, PageResponse } from './api.service';

export interface Order {
    id: string;
    orderNumber: string;
    status: string;
    items: OrderItem[];
    subtotal: number;
    discount: number;
    shippingCost: number;
    total: number;
    deliveryAddress: string;
    deliveryCity: string;
    deliveryState: string;
    deliveryZipCode: string;
    trackingCode?: string;
    estimatedDelivery?: string;
    deliveredAt?: string;
    createdAt: string;
    updatedAt: string;
}

export interface OrderItem {
    id: string;
    productId: string;
    productName: string;
    productSku: string;
    quantity: number;
    unitPrice: number;
    subtotal: number;
}

export interface CreateOrderRequest {
    cartId: string;
    deliveryAddress: string;
    deliveryCity: string;
    deliveryState: string;
    deliveryZipCode: string;
    deliveryRecipientName: string;
    deliveryPhone: string;
    customerNotes?: string;
    couponCode?: string;
}

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    constructor(private api: ApiService) {}

    createOrder(request: CreateOrderRequest): Observable<ApiResponse<Order>> {
        return this.api.post('/orders', request);
    }

    getOrder(id: string): Observable<ApiResponse<Order>> {
        return this.api.get(`/orders/${id}`);
    }

    getOrderByNumber(orderNumber: string): Observable<ApiResponse<Order>> {
        return this.api.get(`/orders/number/${orderNumber}`);
    }

    getUserOrders(page: number = 0): Observable<ApiResponse<PageResponse<Order>>> {
        return this.api.get('/orders', { page });
    }
}
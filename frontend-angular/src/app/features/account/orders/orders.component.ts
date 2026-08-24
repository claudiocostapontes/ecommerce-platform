import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-orders',
    standalone: true,
    imports: [CommonModule, RouterModule],
    template: `
    <div class="orders-container">
      <h1>Meus Pedidos</h1>
      
      <div *ngIf="loading" class="loading">
        <p>Carregando pedidos...</p>
      </div>
      
      <div *ngIf="!loading && orders.length === 0" class="empty-orders">
        <p>Você ainda não possui pedidos.</p>
        <a routerLink="/catalog" class="btn btn-primary">Começar a Comprar</a>
      </div>
      
      <div *ngIf="!loading && orders.length > 0" class="orders-list">
        <div *ngFor="let order of orders" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <h3>Pedido #{{ order.id }}</h3>
              <p class="order-date">{{ order.date | date:'dd/MM/yyyy' }}</p>
            </div>
            <div class="order-status">
              <span [class]="'status-' + order.status.toLowerCase()">{{ order.status }}</span>
            </div>
          </div>
          <div class="order-items">
            <div *ngFor="let item of order.items" class="order-item">
              <span>{{ item.productName }}</span>
              <span>Qtd: {{ item.quantity }}</span>
              <span>R$ {{ item.price | number:'1.2-2' }}</span>
            </div>
          </div>
          <div class="order-total">
            <span>Total:</span>
            <span class="total-amount">R$ {{ order.total | number:'1.2-2' }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
    styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {
    loading = false;
    orders: any[] = [];

    ngOnInit(): void {
        this.loadOrders();
    }

    loadOrders(): void {
        this.loading = true;
        // TODO: Implement orders API call
        console.log('Loading orders');
        setTimeout(() => {
            this.loading = false;
        }, 1000);
    }
}

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { CartService, Cart } from '../../core/services/cart.service';

@Component({
    selector: 'app-checkout',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
    template: `
    <div class="checkout-container">
      <h1>Finalizar Compra</h1>

      <div *ngIf="cart && cart.items.length > 0; else emptyCart" class="checkout-content">
        <!-- Order Summary -->
        <div class="order-summary">
          <h2>Resumo do Pedido</h2>
          <div class="summary-items">
            <div *ngFor="let item of cart.items" class="summary-item">
              <div class="item-info">
                <img [src]="item.productImage" [alt]="item.productName" class="item-thumb">
                <div>
                  <h4>{{ item.productName }}</h4>
                  <p>Qtd: {{ item.quantity }}</p>
                </div>
              </div>
              <span class="item-price">R$ {{ item.subtotal | number: '1.2-2' }}</span>
            </div>
          </div>
          <div class="summary-total">
            <span>Total:</span>
            <span class="total-amount">R$ {{ cart.subtotal | number: '1.2-2' }}</span>
          </div>
        </div>

        <!-- Checkout Form -->
        <div class="checkout-form">
          <h2>Informações de Entrega</h2>
          <form (ngSubmit)="onSubmit()" [formGroup]="checkoutForm">
            <div class="form-group">
              <label for="fullName">Nome Completo</label>
              <input 
                id="fullName" 
                type="text" 
                formControlName="fullName" 
                required>
            </div>
            <div class="form-group">
              <label for="email">E-mail</label>
              <input 
                id="email" 
                type="email" 
                formControlName="email" 
                required>
            </div>
            <div class="form-group">
              <label for="address">Endereço</label>
              <input 
                id="address" 
                type="text" 
                formControlName="address" 
                required>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label for="city">Cidade</label>
                <input 
                  id="city" 
                  type="text" 
                  formControlName="city" 
                  required>
              </div>
              <div class="form-group">
                <label for="zipCode">CEP</label>
                <input 
                  id="zipCode" 
                  type="text" 
                  formControlName="zipCode" 
                  required>
              </div>
            </div>
            <div class="form-group">
              <label for="paymentMethod">Forma de Pagamento</label>
              <select id="paymentMethod" formControlName="paymentMethod" required>
                <option value="">Selecione</option>
                <option value="credit_card">Cartão de Crédito</option>
                <option value="debit_card">Cartão de Débito</option>
                <option value="pix">PIX</option>
              </select>
            </div>
            <button type="submit" class="btn btn-primary btn-block btn-lg" [disabled]="checkoutForm.invalid || loading">
              {{ loading ? 'Processando...' : 'Confirmar Pedido' }}
            </button>
            <button type="button" (click)="goToCart()" class="btn btn-secondary btn-block">
              Voltar ao Carrinho
            </button>
          </form>
        </div>
      </div>

      <ng-template #emptyCart>
        <div class="empty-checkout">
          <p>Seu carrinho está vazio</p>
          <button (click)="goToCatalog()" class="btn btn-primary">
            Voltar ao Catálogo
          </button>
        </div>
      </ng-template>
    </div>
  `,
    styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
    cart: Cart | null = null;
    loading = false;
    checkoutForm: any;

    constructor(
        private cartService: CartService,
        private router: Router
    ) {
        this.checkoutForm = new FormGroup({
            fullName: new FormControl(''),
            email: new FormControl(''),
            address: new FormControl(''),
            city: new FormControl(''),
            zipCode: new FormControl(''),
            paymentMethod: new FormControl('')
        });
    }

    ngOnInit(): void {
        this.loadCart();
        this.cartService.cart$.subscribe(cart => {
            this.cart = cart;
        });
    }

    loadCart(): void {
        this.loading = true;
        this.cartService.getCart().subscribe(
            response => {
                if (response.success) {
                    this.cart = response.data;
                }
                this.loading = false;
            },
            error => {
                console.error('Erro ao carregar carrinho', error);
                this.loading = false;
            }
        );
    }

    onSubmit(): void {
        if (this.checkoutForm.valid && this.cart) {
            this.loading = true;
            // TODO: Implement checkout API call
            console.log('Checkout data:', this.checkoutForm.value);
            setTimeout(() => {
                this.loading = false;
                this.router.navigate(['/account/orders']);
            }, 1500);
        }
    }

    goToCart(): void {
        this.router.navigate(['/cart']);
    }

    goToCatalog(): void {
        this.router.navigate(['/catalog']);
    }
}

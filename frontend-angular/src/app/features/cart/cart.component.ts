import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-cart',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="container">
      <h1>Carrinho de Compras</h1>
      <p>Seu carrinho está vazio.</p>
    </div>
  `,
    styleUrls: ['./cart.component.css']
})
export class CartComponent {}
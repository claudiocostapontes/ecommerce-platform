import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="container">
      <h1>Bem-vindo ao E-commerce</h1>
      <p>Encontre os melhores eletrodomésticos e eletrônicos aqui.</p>
      <a href="/catalog" class="btn btn-primary">Ver Catálogo</a>
    </div>
  `,
    styleUrls: ['./home.component.css']
})
export class HomeComponent {}
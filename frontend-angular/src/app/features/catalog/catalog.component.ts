import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-catalog',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="container">
      <h1>Catálogo de Produtos</h1>
      <p>Aqui você encontrará todos os nossos produtos.</p>
    </div>
  `,
    styleUrls: ['./catalog.component.css']
})
export class CatalogComponent {}
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <header class="header">
      <nav class="navbar">
        <div class="nav-brand">
          <a routerLink="/" class="brand-link">E-commerce</a>
        </div>
        <ul class="nav-links">
          <li><a routerLink="/" routerLinkActive="active">Home</a></li>
          <li><a routerLink="/catalog" routerLinkActive="active">Catalog</a></li>
          <li><a routerLink="/cart" routerLinkActive="active">Cart</a></li>
        </ul>
      </nav>
    </header>
  `,
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {}

import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink],
  template: `
    <header class="site-header">

      <div class="announcement">
        <div class="container announcement-content">
          <span>Compra segura e protegida</span>

          <div class="announcement-links">
            <span>Loja física em Frecheirinha - CE</span>
            <span>Atendimento pelo WhatsApp</span>
          </div>
        </div>
      </div>

      <div class="main-header">
        <div class="container header-content">

          <a
            routerLink="/home"
            class="brand"
            aria-label="Nonato Eletromóveis - Página inicial"
          >
            <img
              src="assets/brand/logononato.jpg"
              alt="Nonato Eletromóveis"
              class="brand-logo"
            />
          </a>

          <div class="search">

            <svg
              viewBox="0 0 24 24"
              width="20"
              height="20"
              aria-hidden="true"
            >
              <path
                d="m21 21-4.35-4.35m2.35-5.65a8 8 0 1 1-16 0 8 8 0 0 1 16 0Z"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
              />
            </svg>

            <input
              type="search"
              placeholder="Busque por produtos, marcas e categorias"
              aria-label="Buscar produtos"
            />

            <button type="button">
              Buscar
            </button>

          </div>

          <nav
            class="actions"
            aria-label="Conta e carrinho"
          >

            <button
              type="button"
              class="action-button account"
              aria-label="Minha conta"
            >
              <svg
                viewBox="0 0 24 24"
                width="22"
                height="22"
                aria-hidden="true"
              >
                <path
                  d="M20 21a8 8 0 0 0-16 0m8-10a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.8"
                  stroke-linecap="round"
                />
              </svg>

              <span>
                <small>Olá, entre ou</small>
                <strong>crie sua conta</strong>
              </span>
            </button>

            <a
              routerLink="/cart"
              class="cart-button"
              aria-label="Abrir carrinho"
            >
              <svg
                viewBox="0 0 24 24"
                width="24"
                height="24"
                aria-hidden="true"
              >
                <path
                  d="M3 4h2l2.2 10.2a2 2 0 0 0 2 1.6h7.9a2 2 0 0 0 2-1.6L20.5 8H6.1M10 20h.01M17 20h.01"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.8"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>

              <span class="cart-badge">
                0
              </span>
            </a>

          </nav>

        </div>
      </div>

      <div class="category-nav">
        <div class="container category-content">

          <a
            routerLink="/catalog"
            class="all-categories"
          >
            <span class="menu-icon">☰</span>
            Todas as categorias
          </a>

          <nav class="category-links">

            <a routerLink="/catalog">
              Eletrodomésticos
            </a>

            <a routerLink="/catalog">
              Eletrônicos
            </a>

            <a routerLink="/catalog">
              Eletroportáteis
            </a>

            <a
              routerLink="/catalog"
              class="offer-link"
            >
              Ofertas
            </a>

          </nav>

        </div>
      </div>

    </header>
  `,
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {}
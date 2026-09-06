import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
    Category,
    Product,
    ProductService
} from '../../core/services/product.service';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [
        CommonModule,
        RouterLink
    ],
    template: `
    <!-- HERO -->
    <section class="hero">
      <div class="container hero-grid">

        <div class="hero-content">
          <span class="hero-eyebrow">
            Nonato Eletromóveis
          </span>

          <h1>
            Sua casa completa
            <span>começa aqui.</span>
          </h1>

          <p class="hero-description">
            Eletrodomésticos, eletrônicos e tecnologia
            para deixar seu lar mais prático, confortável
            e conectado.
          </p>

          <div class="hero-actions">
            <a
              routerLink="/catalog"
              class="hero-primary"
            >
              Ver produtos
              <span aria-hidden="true">→</span>
            </a>

            <a
              href="#ofertas"
              class="hero-secondary"
            >
              Conferir ofertas
            </a>
          </div>

          <div class="hero-trust">
            <div>
              <strong>Loja física</strong>
              <span>Frecheirinha - CE</span>
            </div>

            <div>
              <strong>Compra segura</strong>
              <span>Seus dados protegidos</span>
            </div>

            <div>
              <strong>Atendimento</strong>
              <span>Online e presencial</span>
            </div>
          </div>
        </div>

        <div class="hero-showcase">
          <div class="showcase-decoration"></div>

          <div class="showcase-card">

            <div class="showcase-header">
              <span class="offer-badge">
                Oferta em destaque
              </span>

              <span class="showcase-brand">
                Samsung
              </span>
            </div>

            <div class="tv-product" aria-hidden="true">
              <div class="tv-screen">
                <div class="screen-light light-one"></div>
                <div class="screen-light light-two"></div>

                <div class="screen-text">
                  <small>SMART TV</small>
                  <strong>4K</strong>
                  <span>55 polegadas</span>
                </div>
              </div>

              <div class="tv-base"></div>
            </div>

            <div class="showcase-footer">
              <div>
                <small>Tecnologia para sua casa</small>
                <strong>Imagem que impressiona.</strong>
              </div>

              <span class="showcase-arrow">
                →
              </span>
            </div>

          </div>
        </div>

      </div>
    </section>

    <!-- BENEFÍCIOS -->
    <section class="benefits">
      <div class="container benefits-grid">

        <article class="benefit">
          <span class="benefit-icon">
            ✓
          </span>

          <div>
            <strong>Compra protegida</strong>
            <span>Ambiente seguro para comprar</span>
          </div>
        </article>

        <article class="benefit">
          <span class="benefit-icon">
            ↗
          </span>

          <div>
            <strong>Entrega acompanhada</strong>
            <span>Acompanhe seu pedido</span>
          </div>
        </article>

        <article class="benefit">
          <span class="benefit-icon">
            ♡
          </span>

          <div>
            <strong>Atendimento próximo</strong>
            <span>Fale com nossa equipe</span>
          </div>
        </article>

        <article class="benefit">
          <span class="benefit-icon">
            ◎
          </span>

          <div>
            <strong>Pagamento seguro</strong>
            <span>Compre com tranquilidade</span>
          </div>
        </article>

      </div>
    </section>

    <!-- CATEGORIAS -->
    <section class="categories-section">
      <div class="container">

        <div class="section-heading">
          <div>
            <span class="section-eyebrow">
              Encontre o que procura
            </span>

            <h2>
              Compre por categoria
            </h2>
          </div>

          <a routerLink="/catalog">
            Ver catálogo
            <span>→</span>
          </a>
        </div>

        <div
          *ngIf="categories.length"
          class="category-grid"
        >
          <a
            *ngFor="let category of categories"
            routerLink="/catalog"
            class="category-card"
          >
            <span class="category-icon">
              {{ getCategoryIcon(category.slug) }}
            </span>

            <span class="category-copy">
              <strong>
                {{ category.name }}
              </strong>

              <small>
                Explorar produtos →
              </small>
            </span>
          </a>
        </div>

        <div
          *ngIf="!categories.length"
          class="category-grid category-loading"
        >
          <div
            *ngFor="let item of categorySkeletons"
            class="category-card skeleton-category"
          ></div>
        </div>

      </div>
    </section>

    <!-- PRODUTOS -->
    <section
      id="ofertas"
      class="products-section"
    >
      <div class="container">

        <div class="section-heading">
          <div>
            <span class="section-eyebrow">
              Selecionados para você
            </span>

            <h2>
              Produtos em destaque
            </h2>
          </div>

          <a routerLink="/catalog">
            Ver todos
            <span>→</span>
          </a>
        </div>

        <!-- Loading -->
        <div
          *ngIf="loading"
          class="product-grid"
        >
          <article
            *ngFor="let item of productSkeletons"
            class="product-card skeleton-card"
          >
            <div class="skeleton skeleton-image"></div>

            <div class="skeleton skeleton-line short"></div>
            <div class="skeleton skeleton-line"></div>
            <div class="skeleton skeleton-line medium"></div>
          </article>
        </div>

        <!-- Produtos reais -->
        <div
          *ngIf="!loading && products.length"
          class="product-grid"
        >
          <article
            *ngFor="let product of products"
            class="product-card"
          >
            <div class="product-media">

              <span
                *ngIf="hasPromotion(product)"
                class="discount-badge"
              >
                {{ getDiscount(product) }}
              </span>

              <button
                type="button"
                class="favorite-button"
                aria-label="Adicionar aos favoritos"
              >
                ♡
              </button>

              <img
                *ngIf="product.primaryImage"
                [src]="product.primaryImage"
                [alt]="product.name"
                class="product-image"
              />

              <div
                *ngIf="!product.primaryImage"
                class="product-placeholder"
              >
                <div class="mini-tv">
                  <div class="mini-tv-screen"></div>
                  <div class="mini-tv-base"></div>
                </div>
              </div>

            </div>

            <div class="product-info">

              <span class="product-brand">
                {{ product.brandName || 'Nonato Eletromóveis' }}
              </span>

              <h3>
                {{ product.name }}
              </h3>

              <div class="rating">
                <span class="rating-stars">
                  ★★★★★
                </span>

                <span>
                  {{ product.ratingCount || 0 }}
                  avaliações
                </span>
              </div>

              <div class="pricing">

                <span
                  *ngIf="hasPromotion(product)"
                  class="previous-price"
                >
                  {{
                    product.price
                      | currency:'BRL':'symbol':'1.2-2'
                  }}
                </span>

                <strong class="current-price">
                  {{
                    getCurrentPrice(product)
                      | currency:'BRL':'symbol':'1.2-2'
                  }}
                </strong>

                <span class="installment">
                  ou em até 10x sem juros
                </span>

              </div>

              <a
                routerLink="/catalog"
                class="product-button"
              >
                Ver produto
              </a>

            </div>
          </article>
        </div>

        <!-- Nenhum produto -->
        <div
          *ngIf="!loading && !products.length"
          class="empty-products"
        >
          <span>✦</span>

          <strong>
            Novidades chegando
          </strong>

          <p>
            Estamos preparando uma seleção especial
            para você.
          </p>
        </div>

      </div>
    </section>

    <!-- LOJA FÍSICA -->
    <section class="store-section">
      <div class="container store-card">

        <div class="store-content">
          <span class="section-eyebrow light">
            Estamos perto de você
          </span>

          <h2>
            Compre online ou visite
            nossa loja em Frecheirinha.
          </h2>

          <p>
            Escolha como prefere comprar. Nossa equipe
            está pronta para ajudar você online e
            presencialmente.
          </p>

          <div class="store-actions">
              <a
               href="https://www.google.com/local/place/fid/0x7eb31ec61871c03:0x282ea557ce076d74/photosphere?iu=https://streetviewpixels-pa.googleapis.com/v1/thumbnail?panoid%3Dcs3tRLknDcYsJm0Ov5ne1g%26cb_client%3Dsearch.gws-prod.gps%26yaw%3D21.45813%26pitch%3D0%26thumbfov%3D100%26w%3D0%26h%3D0&ik=CAISFmNzM3RSTGtuRGNZc0ptME92NW5lMWc%3D&sa=X&ved=2ahUKEwim9vztn9iWAxXRALkGHeWcA1cQpx96BAgnEAU"
               target="_blank"
               rel="noopener noreferrer"
               class="store-primary"
              >
                  Ver localização
              </a>

            <a
              href="#"
              class="store-secondary"
            >
              Falar pelo WhatsApp
            </a>
          </div>
        </div>

        <div class="store-visual" aria-hidden="true">

          <div class="location-pin">
            <span></span>
          </div>

          <strong>
            Frecheirinha
          </strong>

          <span>
            Ceará
          </span>

          <div class="location-line"></div>

          <small>
            Nonato Eletromóveis
          </small>

        </div>

      </div>
    </section>
  `,
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

    products: Product[] = [];
    categories: Category[] = [];

    loading = true;

    productSkeletons = [1, 2, 3, 4];
    categorySkeletons = [1, 2, 3];

    constructor(
        private readonly productService: ProductService
    ) {}

    ngOnInit(): void {
        this.loadProducts();
        this.loadCategories();
    }

    hasPromotion(product: Product): boolean {
        return !!product.promotionalPrice &&
            product.promotionalPrice < product.price;
    }

    getCurrentPrice(product: Product): number {
        return this.hasPromotion(product)
            ? product.promotionalPrice!
            : product.price;
    }

    getDiscount(product: Product): string {
        if (product.discountPercentage) {
            return `-${Math.round(product.discountPercentage)}%`;
        }

        if (!this.hasPromotion(product)) {
            return 'Oferta';
        }

        const discount =
            ((product.price - product.promotionalPrice!) /
                product.price) * 100;

        return `-${Math.round(discount)}%`;
    }

    getCategoryIcon(slug: string): string {
        const value = slug?.toLowerCase() ?? '';

        if (value.includes('eletrodomest')) {
            return '⌂';
        }

        if (value.includes('eletronic')) {
            return '▣';
        }

        if (value.includes('eletroport')) {
            return '◉';
        }

        return '✦';
    }

    private loadProducts(): void {
        this.productService
            .getFeaturedProducts()
            .subscribe({
                next: response => {
                    const featured =
                        response.data?.content ?? [];

                    if (featured.length) {
                        this.products = featured;
                        this.loading = false;
                        return;
                    }

                    this.loadAllProducts();
                },

                error: () => {
                    this.loadAllProducts();
                }
            });
    }

    private loadAllProducts(): void {
        this.productService
            .getProducts(0, 8)
            .subscribe({
                next: response => {
                    this.products =
                        response.data?.content ?? [];

                    this.loading = false;
                },

                error: () => {
                    this.products = [];
                    this.loading = false;
                }
            });
    }

    private loadCategories(): void {
        this.productService
            .getCategories()
            .subscribe({
                next: response => {
                    this.categories =
                        response.data ?? [];
                },

                error: () => {
                    this.categories = [];
                }
            });
    }
}
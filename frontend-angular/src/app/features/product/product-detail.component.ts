import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ProductService, ProductDetail } from '../../core/services/product.service';

@Component({
    selector: 'app-product-detail',
    standalone: true,
    imports: [CommonModule, RouterModule],
    template: `
    <div class="product-detail-container" *ngIf="product; else loading">
      <div class="product-detail-content">
        <!-- Product Images -->
        <div class="product-images">
          <img [src]="product.primaryImage" [alt]="product.name" class="main-image">
        </div>

        <!-- Product Info -->
        <div class="product-info">
          <p class="brand">{{ product.brandName }}</p>
          <h1>{{ product.name }}</h1>
          <p class="description">{{ product.description }}</p>

          <div class="pricing">
            <span *ngIf="product.onPromotion" class="original-price">
              R$ {{ product.price | number: '1.2-2' }}
            </span>
            <span class="price">R$ {{ product.promotionalPrice || product.price | number: '1.2-2' }}</span>
            <span *ngIf="product.discountPercentage" class="discount-badge">
              -{{ product.discountPercentage }}%
            </span>
          </div>

          <div class="stock-info">
            <span [class.in-stock]="product.stock > 0" [class.out-stock]="product.stock === 0">
              {{ product.stock > 0 ? 'Em estoque: ' + product.stock + ' unidades' : 'Fora de estoque' }}
            </span>
          </div>

          <div class="actions">
            <button 
              [disabled]="product.stock === 0 || addingToCart"
              (click)="addToCart()"
              class="btn btn-primary btn-large">
              {{ addingToCart ? 'Adicionando...' : 'Adicionar ao Carrinho' }}
            </button>
            <a routerLink="/catalog" class="btn btn-secondary">Voltar ao Catálogo</a>
          </div>
        </div>
      </div>
    </div>

    <ng-template #loading>
      <div class="loading-container">
        <p>Carregando produto...</p>
      </div>
    </ng-template>
  `,
    styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
    product: ProductDetail | null = null;
    loading = false;
    addingToCart = false;

    constructor(
        private productService: ProductService,
        private route: ActivatedRoute
    ) {}

    ngOnInit(): void {
        this.loadProduct();
    }

    loadProduct(): void {
        this.loading = true;
        const slug = this.route.snapshot.paramMap.get('slug');
        
        if (slug) {
            this.productService.getProductBySlug(slug).subscribe(
                response => {
                    if (response.success) {
                        this.product = response.data;
                    }
                    this.loading = false;
                },
                error => {
                    console.error('Erro ao carregar produto', error);
                    this.loading = false;
                }
            );
        }
    }

    addToCart(): void {
        if (!this.product) return;
        
        this.addingToCart = true;
        // TODO: Implement cart service integration
        setTimeout(() => {
            this.addingToCart = false;
        }, 1000);
    }
}

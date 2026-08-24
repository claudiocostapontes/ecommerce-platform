import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService, ApiResponse, PageResponse } from './api.service';

export interface Product {
    id: string;
    name: string;
    slug: string;
    sku: string;
    shortDescription: string;
    price: number;
    promotionalPrice?: number;
    discountPercentage?: number;
    onPromotion: boolean;
    categoryName: string;
    categoryId: string;
    brandName: string;
    brandId: string;
    primaryImage: string;
    ratingAverage: number;
    ratingCount: number;
}

export interface ProductDetail extends Product {
    description: string;
    ean: string;
    weight: number;
    width: number;
    height: number;
    depth: number;
    images: ProductImage[];
    specifications: Record<string, any>;
    featured: boolean;
    viewCount: number;
    stock: number;
    createdAt: string;
    updatedAt: string;
}

export interface ProductImage {
    id: string;
    imageUrl: string;
    thumbnailUrl: string;
    altText: string;
    displayOrder: number;
    isPrimary: boolean;
}

export interface Category {
    id: string;
    name: string;
    slug: string;
    description?: string;
    imageUrl?: string;
    subcategories?: Category[];
}

export interface Brand {
    id: string;
    name: string;
    slug: string;
    logoUrl?: string;
}

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    constructor(private api: ApiService) {}

    getProducts(page: number = 0, size: number = 20): Observable<ApiResponse<PageResponse<Product>>> {
        return this.api.get('/products', { page, size });
    }

    getProductById(id: string): Observable<ApiResponse<ProductDetail>> {
        return this.api.get(`/products/${id}`);
    }

    getProductBySlug(slug: string): Observable<ApiResponse<ProductDetail>> {
        return this.api.get(`/products/slug/${slug}`);
    }

    getProductsByCategory(categoryId: string, page: number = 0): Observable<ApiResponse<PageResponse<Product>>> {
        return this.api.get(`/products/category/${categoryId}`, { page });
    }

    getProductsByBrand(brandId: string, page: number = 0): Observable<ApiResponse<PageResponse<Product>>> {
        return this.api.get(`/products/brand/${brandId}`, { page });
    }

    searchProducts(query: string, page: number = 0): Observable<ApiResponse<PageResponse<Product>>> {
        return this.api.get('/products/search', { q: query, page });
    }

    filterProducts(
        categoryId?: string,
        brandId?: string,
        minPrice?: number,
        maxPrice?: number,
        search?: string,
        page: number = 0
    ): Observable<ApiResponse<PageResponse<Product>>> {
        return this.api.get('/products/filter', {
            categoryId,
            brandId,
            minPrice,
            maxPrice,
            search,
            page
        });
    }

    getFeaturedProducts(): Observable<ApiResponse<PageResponse<Product>>> {
        return this.api.get('/products/featured');
    }

    getCategories(): Observable<ApiResponse<Category[]>> {
        return this.api.get('/categories');
    }

    getCategoryById(id: string): Observable<ApiResponse<Category>> {
        return this.api.get(`/categories/${id}`);
    }

    getBrands(): Observable<ApiResponse<Brand[]>> {
        return this.api.get('/brands/active');
    }
}
import { api, ApiResponse, PageResponse } from './api'

export interface Product {
    id: string
    name: string
    slug: string
    sku: string
    ean?: string
    shortDescription: string
    description: string
    price: number
    promotionalPrice?: number
    categoryId: string
    categoryName: string
    brandId?: string
    brandName?: string
    weight: number
    width: number
    height: number
    depth: number
    stock: number
    images: ProductImage[]
    specifications: Record<string, any>
    active: boolean
    featured: boolean
    ratingAverage: number
    ratingCount: number
    createdAt: string
    updatedAt: string
}

export interface ProductImage {
    imageUrl: string
    thumbnailUrl?: string
    altText?: string
    displayOrder: number
    isPrimary: boolean
}

export interface CreateProductRequest {
    name: string
    sku: string
    ean?: string
    description: string
    shortDescription: string
    price: number
    promotionalPrice?: number
    categoryId: string
    brandId?: string
    weight: number
    width: number
    height: number
    depth: number
    images: ProductImage[]
    specifications: Record<string, any>
    featured: boolean
}

export const productService = {
    getProducts: (page: number = 0, size: number = 20) =>
        api.get<PageResponse<Product>>('/products', { page, size }),

    getProduct: (id: string) =>
        api.get<Product>(`/products/${id}`),

    createProduct: (data: CreateProductRequest) =>
        api.post<Product>('/products', data),

    updateProduct: (id: string, data: CreateProductRequest) =>
        api.put<Product>(`/products/${id}`, data),

    deleteProduct: (id: string) =>
        api.delete(`/products/${id}`),

    activateProduct: (id: string) =>
        api.patch(`/products/${id}/activate`, {})
}
import { Routes } from '@angular/router';

export const appRoutes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    {
        path: 'home',
        loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
    },
    {
        path: 'catalog',
        loadComponent: () => import('./features/catalog/catalog.component').then(m => m.CatalogComponent)
    },
    {
        path: 'cart',
        loadComponent: () => import('./features/cart/cart.component').then(m => m.CartComponent)
    }
];
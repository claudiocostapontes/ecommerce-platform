import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import Dashboard from '../views/Dashboard.vue'
import LoginView from '../views/login/LoginView.vue'
import ProductList from '../views/products/ProductList.vue'
import ProductCreate from '../views/products/ProductCreate.vue'
import ProductEdit from '../views/products/ProductEdit.vue'
import OrderList from '../views/orders/OrderList.vue'
import OrderDetail from '../views/orders/OrderDetail.vue'
import CustomerList from '../views/customers/CustomerList.vue'
import InventoryList from '../views/inventory/InventoryList.vue'
import SalesReport from '../views/reports/SalesReport.vue'

const routes = [
    {
        path: '/login',
        component: LoginView,
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        component: Dashboard,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
    },
    {
        path: '/products',
        component: ProductList,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
    },
    {
        path: '/products/create',
        component: ProductCreate,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
    },
    {
        path: '/products/:id/edit',
        component: ProductEdit,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
    },
    {
        path: '/orders',
        component: OrderList,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR'] }
    },
    {
        path: '/orders/:id',
        component: OrderDetail,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR'] }
    },
    {
        path: '/customers',
        component: CustomerList,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
    },
    {
        path: '/inventory',
        component: InventoryList,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR'] }
    },
    {
        path: '/reports/sales',
        component: SalesReport,
        meta: { requiresAuth: true, roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, _from, next) => {
    const authStore = useAuthStore()

    if (to.meta.requiresAuth) {
        if (!authStore.isAuthenticated) {
            next('/login')
        } else if (to.meta.roles && !to.meta.roles.some(role => authStore.user?.roles.includes(role))) {
            next('/')
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router
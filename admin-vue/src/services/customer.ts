import { api, PageResponse } from './api'

export interface Customer {
    id: string
    name: string
    email: string
    phone?: string
    cpf?: string
    active: boolean
    createdAt: string
    updatedAt: string
}

export const customerService = {
    getCustomers: (page: number = 0, size: number = 20) =>
        api.get<PageResponse<Customer>>('/customers', { page, size }),

    getCustomer: (id: string) =>
        api.get<Customer>(`/customers/${id}`),

    deleteCustomer: (id: string) =>
        api.delete(`/customers/${id}`),

    activateCustomer: (id: string) =>
        api.patch(`/customers/${id}/activate`, {})
}

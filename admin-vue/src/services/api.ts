import axios from 'axios'

const apiClient = axios.create({
    baseURL: '/api/v1',
    headers: {
        'Content-Type': 'application/json'
    }
})

export interface ApiResponse<T> {
    success: boolean
    message?: string
    data: T
}

export interface PageResponse<T> {
    content: T[]
    pageNumber: number
    pageSize: number
    totalElements: number
    totalPages: number
    first: boolean
    last: boolean
    empty: boolean
}

export const api = {
    get: <T,>(endpoint: string, params?: any) =>
        apiClient.get<ApiResponse<T>>(endpoint, { params }),

    post: <T,>(endpoint: string, data: any) =>
        apiClient.post<ApiResponse<T>>(endpoint, data),

    put: <T,>(endpoint: string, data: any) =>
        apiClient.put<ApiResponse<T>>(endpoint, data),

    delete: <T,>(endpoint: string) =>
        apiClient.delete<ApiResponse<T>>(endpoint),

    patch: <T,>(endpoint: string, data: any) =>
        apiClient.patch<ApiResponse<T>>(endpoint, data)
}

export default apiClient
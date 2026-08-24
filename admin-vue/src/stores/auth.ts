import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

interface User {
    id: string
    username: string
    email: string
    fullName: string
    roles: string[]
}

interface AuthResponse {
    accessToken: string
    refreshToken: string
    user: User
}

export const useAuthStore = defineStore('auth', () => {
    const user = ref<User | null>(null)
    const token = ref<string | null>(localStorage.getItem('admin_token'))
    const loading = ref(false)
    const error = ref<string | null>(null)

    const isAuthenticated = computed(() => !!token.value)

    const login = async (usernameOrEmail: string, password: string) => {
        loading.value = true
        error.value = null

        try {
            const response = await axios.post<AuthResponse>('/api/v1/auth/login', {
                usernameOrEmail,
                password
            })

            const { accessToken, user: userData } = response.data
            token.value = accessToken
            user.value = userData

            localStorage.setItem('admin_token', accessToken)
            localStorage.setItem('admin_user', JSON.stringify(userData))

            // Set auth header
            axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`

            return true
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Login failed'
            return false
        } finally {
            loading.value = false
        }
    }

    const logout = () => {
        user.value = null
        token.value = null
        localStorage.removeItem('admin_token')
        localStorage.removeItem('admin_user')
        delete axios.defaults.headers.common['Authorization']
    }

    const loadUser = () => {
        const storedUser = localStorage.getItem('admin_user')
        if (storedUser && token.value) {
            user.value = JSON.parse(storedUser)
            axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
        }
    }

    const hasRole = (role: string) => {
        return user.value?.roles.includes(role) || false
    }

    loadUser()

    return {
        user,
        token,
        loading,
        error,
        isAuthenticated,
        login,
        logout,
        loadUser,
        hasRole
    }
})
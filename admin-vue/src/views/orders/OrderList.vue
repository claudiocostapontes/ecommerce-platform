<template>
  <Layout>
    <div class="orders-page">
      <div class="page-header">
        <h1>Pedidos</h1>
      </div>

      <div class="filters">
        <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar pedidos..."
            class="form-control"
            @input="onSearch">
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>Cliente</th>
            <th>Data</th>
            <th>Status</th>
            <th>Total</th>
            <th>Ações</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td>{{ order.id }}</td>
            <td>{{ order.customerName }}</td>
            <td>{{ formatDate(order.createdAt) }}</td>
            <td>
                <span class="badge" :class="getStatusBadgeClass(order.status)">
                  {{ getStatusLabel(order.status) }}
                </span>
            </td>
            <td>R$ {{ order.total.toFixed(2) }}</td>
            <td>
              <router-link :to="`/orders/${order.id}`" class="btn btn-sm btn-secondary">
                Detalhes
              </router-link>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="pagination" v-if="totalPages > 1">
        <button
            v-for="page in totalPages"
            :key="page"
            @click="currentPage = page - 1"
            :class="{ active: currentPage === page - 1 }"
            class="page-btn">
          {{ page }}
        </button>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Layout from '../../components/layout/Layout.vue'

interface Order {
  id: string
  customerName: string
  createdAt: string
  status: string
  total: number
}

const orders = ref<Order[]>([])
const searchQuery = ref('')
const currentPage = ref(0)
const totalPages = ref(1)
const loading = ref(false)

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    // TODO: Implement actual API call
    // const response = await orderService.getOrders(currentPage.value, 20)
    // orders.value = response.data.data.content
    // totalPages.value = response.data.data.totalPages
  } catch (error) {
    console.error('Error loading orders:', error)
  } finally {
    loading.value = false
  }
}

const onSearch = async () => {
  currentPage.value = 0
  loadOrders()
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('pt-BR')
}

const getStatusBadgeClass = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': 'badge-warning',
    'PROCESSING': 'badge-info',
    'SHIPPED': 'badge-primary',
    'DELIVERED': 'badge-success',
    'CANCELLED': 'badge-danger'
  }
  return statusMap[status] || 'badge-secondary'
}

const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': ' pending',
    'PROCESSING': 'Processando',
    'SHIPPED': 'Enviado',
    'DELIVERED': 'Entregue',
    'CANCELLED': 'Cancelado'
  }
  return statusMap[status] || status
}
</script>

<style scoped>
.orders-page {
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.filters {
  margin-bottom: 2rem;
}

.form-control {
  width: 100%;
  max-width: 300px;
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f3f4f6;
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid #e5e7eb;
}

.data-table td {
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
}

.btn-secondary {
  background: #6b7280;
  color: white;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.badge-success { background: #d1fae5; color: #065f46; }
.badge-warning { background: #fef3c7; color: #92400e; }
.badge-danger { background: #fee2e2; color: #991b1b; }
.badge-info { background: #dbeafe; color: #1e40af; }
.badge-primary { background: #e0e7ff; color: #3730a3; }
.badge-secondary { background: #f3f4f6; color: #374151; }

.pagination {
  display: flex;
  gap: 0.5rem;
  margin-top: 2rem;
  justify-content: center;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  background: white;
  cursor: pointer;
  border-radius: 4px;
}

.page-btn.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}
</style>

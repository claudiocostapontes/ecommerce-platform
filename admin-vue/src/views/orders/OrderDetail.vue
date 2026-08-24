<template>
  <Layout>
    <div class="order-detail-page">
      <div class="page-header">
        <h1>Detalhes do Pedido #{{ orderId }}</h1>
        <router-link to="/orders" class="btn btn-secondary">
          Voltar
        </router-link>
      </div>

      <div v-if="loading" class="loading">Carregando...</div>

      <div v-else-if="order" class="order-content">
        <div class="order-info">
          <div class="info-card">
            <h3>Informações do Pedido</h3>
            <p><strong>Status:</strong> <span class="badge" :class="getStatusClass(order.status)">{{ order.status }}</span></p>
            <p><strong>Data:</strong> {{ formatDate(order.createdAt) }}</p>
            <p><strong>Total:</strong> R$ {{ order.total?.toFixed(2) }}</p>
          </div>

          <div class="info-card">
            <h3>Informações do Cliente</h3>
            <p><strong>Nome:</strong> {{ order.customerName }}</p>
            <p><strong>Email:</strong> {{ order.customerEmail }}</p>
          </div>
        </div>

        <div class="order-items">
          <h3>Itens do Pedido</h3>
          <table class="data-table">
            <thead>
              <tr>
                <th>Produto</th>
                <th>Quantidade</th>
                <th>Preço Unitário</th>
                <th>Subtotal</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in order.items" :key="item.id">
                <td>{{ item.productName }}</td>
                <td>{{ item.quantity }}</td>
                <td>R$ {{ item.price?.toFixed(2) }}</td>
                <td>R$ {{ (item.quantity * item.price)?.toFixed(2) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Layout from '../../components/layout/Layout.vue'

const route = useRoute()
const orderId = route.params.id as string

const order = ref<any>(null)
const loading = ref(false)

onMounted(() => {
  loadOrder()
})

const loadOrder = async () => {
  loading.value = true
  try {
    // TODO: Implement API call to fetch order details
    // const response = await api.get(`/orders/${orderId}`)
    // order.value = response.data.data
  } catch (error) {
    console.error('Error loading order:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('pt-BR')
}

const getStatusClass = (status: string) => {
  switch (status) {
    case 'COMPLETED': return 'badge-success'
    case 'PENDING': return 'badge-warning'
    case 'CANCELLED': return 'badge-danger'
    default: return 'badge-warning'
  }
}
</script>

<style scoped>
.order-detail-page {
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.loading {
  text-align: center;
  padding: 2rem;
}

.order-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.info-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.info-card h3 {
  margin-bottom: 1rem;
}

.order-items {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
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
  text-decoration: none;
}

.btn-secondary {
  background: #6b7280;
  color: white;
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
</style>

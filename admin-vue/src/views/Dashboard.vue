<template>
  <Layout>
    <div class="dashboard">
      <h1>Dashboard</h1>

      <div class="metrics-grid">
        <div class="metric-card">
          <h3>Vendas Hoje</h3>
          <p class="metric-value">R$ {{ todayRevenue }}</p>
          <small>+{{ todayOrders }} pedidos</small>
        </div>

        <div class="metric-card">
          <h3>Vendas Mês</h3>
          <p class="metric-value">R$ {{ monthRevenue }}</p>
          <small>+{{ monthOrders }} pedidos</small>
        </div>

        <div class="metric-card">
          <h3>Produtos Ativos</h3>
          <p class="metric-value">{{ activeProducts }}</p>
          <small>+{{ inactiveProducts }} inativos</small>
        </div>

        <div class="metric-card">
          <h3>Clientes</h3>
          <p class="metric-value">{{ totalCustomers }}</p>
          <small>Total registrado</small>
        </div>
      </div>

      <div class="charts-grid">
        <div class="chart-container">
          <h3>Vendas últimos 7 dias</h3>
          <canvas ref="salesChart"></canvas>
        </div>

        <div class="chart-container">
          <h3>Top 5 Produtos</h3>
          <canvas ref="topProductsChart"></canvas>
        </div>
      </div>

      <div class="recent-orders">
        <h3>Pedidos Recentes</h3>
        <table class="data-table">
          <thead>
          <tr>
            <th>Pedido</th>
            <th>Cliente</th>
            <th>Total</th>
            <th>Status</th>
            <th>Data</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="order in recentOrders" :key="order.id">
            <td><strong>{{ order.orderNumber }}</strong></td>
            <td>{{ order.customerName }}</td>
            <td>R$ {{ order.total }}</td>
            <td><span :class="'badge badge-' + order.statusColor">{{ order.status }}</span></td>
            <td>{{ formatDate(order.createdAt) }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Chart } from 'chart.js/auto'
import Layout from '../components/layout/Layout.vue'
import { api, PageResponse } from '../services/api'

interface Order {
  id: string
  orderNumber: string
  customerName: string
  total: number
  status: string
  statusColor: string
  createdAt: string
}

interface Product {
  id: string
  active: boolean
}

const todayRevenue = ref('0')
const todayOrders = ref(0)
const monthRevenue = ref('0')
const monthOrders = ref(0)
const activeProducts = ref(0)
const inactiveProducts = ref(0)
const totalCustomers = ref(0)
const recentOrders = ref<Order[]>([])

const salesChart = ref<HTMLCanvasElement | null>(null)
const topProductsChart = ref<HTMLCanvasElement | null>(null)

onMounted(() => {
  loadDashboardData()
  initCharts()
})

const loadDashboardData = async () => {
  try {
    // Load metrics
    const ordersResponse = await api.get<PageResponse<Order>>('/orders')
    const productsResponse = await api.get<PageResponse<Product>>('/products')

    // Process data and update refs
    recentOrders.value = ordersResponse.data.data.content.slice(0, 5)
    
    const products = productsResponse.data.data.content
    activeProducts.value = products.filter((p: Product) => p.active).length
    inactiveProducts.value = products.filter((p: Product) => !p.active).length
  } catch (error) {
    console.error('Error loading dashboard data:', error)
  }
}

const initCharts = () => {
  if (salesChart.value) {
    new Chart(salesChart.value, {
      type: 'line',
      data: {
        labels: ['Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sab', 'Dom'],
        datasets: [{
          label: 'Vendas',
          data: [1000, 1500, 1200, 2000, 1800, 2500, 2000],
          borderColor: '#3b82f6',
          tension: 0.4
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true }
        }
      }
    })
  }

  if (topProductsChart.value) {
    new Chart(topProductsChart.value, {
      type: 'bar',
      data: {
        labels: ['Produto 1', 'Produto 2', 'Produto 3', 'Produto 4', 'Produto 5'],
        datasets: [{
          label: 'Vendas',
          data: [150, 120, 100, 90, 80],
          backgroundColor: '#3b82f6'
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false }
        }
      }
    })
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('pt-BR')
}
</script>

<style scoped>
.dashboard {
  padding: 2rem;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.metric-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.metric-value {
  font-size: 2rem;
  font-weight: bold;
  color: #3b82f6;
  margin: 1rem 0;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.chart-container {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.recent-orders {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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
}

.data-table td {
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 600;
}

.badge-success { background: #d1fae5; color: #065f46; }
.badge-warning { background: #fef3c7; color: #92400e; }
.badge-danger { background: #fee2e2; color: #991b1b; }
</style>
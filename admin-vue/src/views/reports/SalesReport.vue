<template>
  <Layout>
    <div class="sales-report-page">
      <div class="page-header">
        <h1>Relatório de Vendas</h1>
      </div>

      <div class="filters">
        <div class="filter-group">
          <label>Data Inicial:</label>
          <input v-model="startDate" type="date" class="form-control">
        </div>
        <div class="filter-group">
          <label>Data Final:</label>
          <input v-model="endDate" type="date" class="form-control">
        </div>
        <button @click="loadSalesData" class="btn btn-primary">
          Gerar Relatório
        </button>
      </div>

      <div class="metrics-grid">
        <div class="metric-card">
          <h3>Total de Vendas</h3>
          <p class="metric-value">R$ {{ totalSales }}</p>
          <small>{{ totalOrders }} pedidos</small>
        </div>

        <div class="metric-card">
          <h3>Ticket Médio</h3>
          <p class="metric-value">R$ {{ averageOrderValue }}</p>
          <small>Por pedido</small>
        </div>

        <div class="metric-card">
          <h3>Produtos Vendidos</h3>
          <p class="metric-value">{{ totalProductsSold }}</p>
          <small>Unidades</small>
        </div>
      </div>

      <div class="chart-container">
        <h3>Vendas por Período</h3>
        <canvas ref="salesChart"></canvas>
      </div>

      <div class="table-container">
        <h3>Vendas Detalhadas</h3>
        <table class="data-table">
          <thead>
          <tr>
            <th>Pedido</th>
            <th>Cliente</th>
            <th>Data</th>
            <th>Itens</th>
            <th>Total</th>
            <th>Status</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="sale in sales" :key="sale.id">
            <td><strong>{{ sale.orderNumber }}</strong></td>
            <td>{{ sale.customerName }}</td>
            <td>{{ formatDate(sale.createdAt) }}</td>
            <td>{{ sale.items }}</td>
            <td>R$ {{ sale.total.toFixed(2) }}</td>
            <td>
              <span class="badge" :class="getStatusClass(sale.status)">
                {{ sale.status }}
              </span>
            </td>
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
import Layout from '../../components/layout/Layout.vue'
import { api, PageResponse } from '../../services/api'

const startDate = ref('')
const endDate = ref('')
const totalSales = ref('0')
const totalOrders = ref(0)
const averageOrderValue = ref('0')
const totalProductsSold = ref(0)
const sales = ref<any[]>([])

const salesChart = ref<HTMLCanvasElement | null>(null)

onMounted(() => {
  const today = new Date()
  const lastMonth = new Date()
  lastMonth.setDate(today.getDate() - 30)
  
  endDate.value = today.toISOString().split('T')[0]
  startDate.value = lastMonth.toISOString().split('T')[0]
  
  loadSalesData()
})

const loadSalesData = async () => {
  try {
    const response = await api.get<PageResponse<any>>('/orders', {
      params: {
        startDate: startDate.value,
        endDate: endDate.value
      }
    })
    
    sales.value = response.data.data.content
    calculateMetrics()
    initChart()
  } catch (error) {
    console.error('Error loading sales data:', error)
  }
}

const calculateMetrics = () => {
  totalOrders.value = sales.value.length
  const total = sales.value.reduce((sum, sale) => sum + sale.total, 0)
  totalSales.value = total.toFixed(2)
  averageOrderValue.value = totalOrders.value > 0 ? (total / totalOrders.value).toFixed(2) : '0'
  totalProductsSold.value = sales.value.reduce((sum, sale) => sum + (sale.items || 0), 0)
}

const initChart = () => {
  if (salesChart.value) {
    const salesByDate = sales.value.reduce((acc: any, sale: any) => {
      const date = new Date(sale.createdAt).toLocaleDateString('pt-BR')
      acc[date] = (acc[date] || 0) + sale.total
      return acc
    }, {})

    new Chart(salesChart.value, {
      type: 'line',
      data: {
        labels: Object.keys(salesByDate),
        datasets: [{
          label: 'Vendas (R$)',
          data: Object.values(salesByDate),
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          tension: 0.4,
          fill: true
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true }
        },
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    })
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('pt-BR')
}

const getStatusClass = (status: string) => {
  const statusMap: any = {
    'COMPLETED': 'badge-success',
    'PENDING': 'badge-warning',
    'CANCELLED': 'badge-danger'
  }
  return statusMap[status] || 'badge-warning'
}
</script>

<style scoped>
.sales-report-page {
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.875rem;
  font-weight: 600;
}

.form-control {
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
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

.chart-container {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
}

.table-container {
  background: white;
  padding: 2rem;
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
}

.btn-primary {
  background: #3b82f6;
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

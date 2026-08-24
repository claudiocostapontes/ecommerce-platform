<template>
  <Layout>
    <div class="inventory-page">
      <div class="page-header">
        <h1>Estoque</h1>
      </div>

      <div class="filters">
        <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar no estoque..."
            class="form-control"
            @input="onSearch">
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
          <tr>
            <th>Produto</th>
            <th>SKU</th>
            <th>Quantidade</th>
            <th>Localização</th>
            <th>Status</th>
            <th>Última Atualização</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="item in inventoryItems" :key="item.id">
            <td>
              <div class="product-info">
                <div>
                  <strong>{{ item.productName }}</strong>
                </div>
              </div>
            </td>
            <td>{{ item.sku }}</td>
            <td>
                <span class="badge" :class="getStockBadgeClass(item.quantity)">
                  {{ item.quantity }}
                </span>
            </td>
            <td>{{ item.location || '-' }}</td>
            <td>
                <span class="badge" :class="item.status === 'AVAILABLE' ? 'badge-success' : 'badge-warning'">
                  {{ item.status === 'AVAILABLE' ? 'Disponível' : 'Reservado' }}
                </span>
            </td>
            <td>{{ formatDate(item.lastUpdated) }}</td>
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

interface InventoryItem {
  id: string
  productName: string
  sku: string
  quantity: number
  location: string
  status: 'AVAILABLE' | 'RESERVED'
  lastUpdated: string
}

const inventoryItems = ref<InventoryItem[]>([])
const searchQuery = ref('')
const currentPage = ref(0)
const totalPages = ref(1)
const loading = ref(false)

onMounted(() => {
  loadInventory()
})

const loadInventory = async () => {
  loading.value = true
  try {
    // TODO: Replace with actual API call
    // const response = await inventoryService.getInventory(currentPage.value, 20)
    // inventoryItems.value = response.data.data.content
    // totalPages.value = response.data.data.totalPages
    
    // Mock data for now
    inventoryItems.value = [
      {
        id: '1',
        productName: 'Produto Exemplo 1',
        sku: 'SKU001',
        quantity: 50,
        location: 'A1-01',
        status: 'AVAILABLE',
        lastUpdated: new Date().toISOString()
      }
    ]
  } catch (error) {
    console.error('Error loading inventory:', error)
  } finally {
    loading.value = false
  }
}

const onSearch = async () => {
  currentPage.value = 0
  loadInventory()
}

const getStockBadgeClass = (quantity: number) => {
  if (quantity === 0) return 'badge-danger'
  if (quantity < 10) return 'badge-warning'
  return 'badge-success'
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('pt-BR')
}
</script>

<style scoped>
.inventory-page {
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

.product-info {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
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

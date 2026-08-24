<template>
  <Layout>
    <div class="customers-page">
      <div class="page-header">
        <h1>Clientes</h1>
      </div>

      <div class="filters">
        <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar clientes..."
            class="form-control"
            @input="onSearch">
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
          <tr>
            <th>Nome</th>
            <th>Email</th>
            <th>Telefone</th>
            <th>CPF</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="customer in customers" :key="customer.id">
            <td>
              <strong>{{ customer.name }}</strong>
            </td>
            <td>{{ customer.email }}</td>
            <td>{{ customer.phone || '-' }}</td>
            <td>{{ customer.cpf || '-' }}</td>
            <td>
                <span class="badge" :class="customer.active ? 'badge-success' : 'badge-warning'">
                  {{ customer.active ? 'Ativo' : 'Inativo' }}
                </span>
            </td>
            <td>
              <button @click="deleteCustomer(customer.id)" class="btn btn-sm btn-danger">
                Deletar
              </button>
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
import { customerService, Customer } from '../../services/customer'

const customers = ref<Customer[]>([])
const searchQuery = ref('')
const currentPage = ref(0)
const totalPages = ref(1)
const loading = ref(false)

onMounted(() => {
  loadCustomers()
})

const loadCustomers = async () => {
  loading.value = true
  try {
    const response = await customerService.getCustomers(currentPage.value, 20)
    customers.value = response.data.data.content
    totalPages.value = response.data.data.totalPages
  } catch (error) {
    console.error('Error loading customers:', error)
  } finally {
    loading.value = false
  }
}

const onSearch = async () => {
  currentPage.value = 0
  loadCustomers()
}

const deleteCustomer = async (id: string) => {
  if (confirm('Deseja deletar este cliente?')) {
    try {
      await customerService.deleteCustomer(id)
      loadCustomers()
    } catch (error) {
      console.error('Error deleting customer:', error)
    }
  }
}
</script>

<style scoped>
.customers-page {
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

.btn-danger {
  background: #ef4444;
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

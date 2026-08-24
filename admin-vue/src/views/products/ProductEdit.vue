<template>
  <Layout>
    <div class="product-edit-page">
      <div class="page-header">
        <h1>Editar Produto</h1>
        <router-link to="/products" class="btn btn-secondary">
          Voltar
        </router-link>
      </div>

      <div v-if="loading" class="loading">Carregando...</div>

      <form v-else @submit.prevent="saveProduct" class="product-form">
        <div class="form-group">
          <label>Nome</label>
          <input v-model="product.name" type="text" class="form-control" required>
        </div>

        <div class="form-group">
          <label>SKU</label>
          <input v-model="product.sku" type="text" class="form-control" required>
        </div>

        <div class="form-group">
          <label>Descrição Curta</label>
          <textarea v-model="product.shortDescription" class="form-control" rows="2"></textarea>
        </div>

        <div class="form-group">
          <label>Descrição</label>
          <textarea v-model="product.description" class="form-control" rows="4"></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Preço</label>
            <input v-model.number="product.price" type="number" step="0.01" class="form-control" required>
          </div>

          <div class="form-group">
            <label>Estoque</label>
            <input v-model.number="product.stock" type="number" class="form-control" required>
          </div>
        </div>

        <div class="form-group">
          <label>Categoria</label>
          <input v-model="product.categoryName" type="text" class="form-control">
        </div>

        <div class="form-group">
          <label>
            <input v-model="product.active" type="checkbox">
            Ativo
          </label>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary">Salvar</button>
          <router-link to="/products" class="btn btn-secondary">Cancelar</router-link>
        </div>
      </form>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Layout from '../../components/layout/Layout.vue'
import { productService, Product } from '../../services/product'

const route = useRoute()
const router = useRouter()

const product = ref<Partial<Product>>({
  name: '',
  sku: '',
  shortDescription: '',
  description: '',
  price: 0,
  stock: 0,
  categoryName: '',
  active: true
})
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const response = await productService.getProduct(route.params.id as string)
    product.value = response.data.data
  } catch (error) {
    console.error('Error loading product:', error)
  } finally {
    loading.value = false
  }
})

const saveProduct = async () => {
  try {
    await productService.updateProduct(route.params.id as string, product.value as Product)
    router.push('/products')
  } catch (error) {
    console.error('Error saving product:', error)
  }
}
</script>

<style scoped>
.product-edit-page {
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

.product-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  max-width: 800px;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.form-control {
  width: 100%;
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 1rem;
}

.form-control[type="checkbox"] {
  width: auto;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  text-decoration: none;
  display: inline-block;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-secondary {
  background: #6b7280;
  color: white;
}
</style>

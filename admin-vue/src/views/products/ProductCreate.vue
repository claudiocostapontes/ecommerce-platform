<template>
  <Layout>
    <div class="product-create-page">
      <div class="page-header">
        <h1>Novo Produto</h1>
        <router-link to="/products" class="btn btn-secondary">
          Voltar
        </router-link>
      </div>

      <form @submit.prevent="handleSubmit" class="product-form">
        <div class="form-section">
          <h2>Informações Básicas</h2>
          <div class="form-group">
            <label for="name">Nome do Produto *</label>
            <input
              id="name"
              v-model="formData.name"
              type="text"
              class="form-control"
              required>
          </div>
          <div class="form-group">
            <label for="sku">SKU *</label>
            <input
              id="sku"
              v-model="formData.sku"
              type="text"
              class="form-control"
              required>
          </div>
          <div class="form-group">
            <label for="ean">EAN</label>
            <input
              id="ean"
              v-model="formData.ean"
              type="text"
              class="form-control">
          </div>
          <div class="form-group">
            <label for="shortDescription">Descrição Curta *</label>
            <input
              id="shortDescription"
              v-model="formData.shortDescription"
              type="text"
              class="form-control"
              required>
          </div>
          <div class="form-group">
            <label for="description">Descrição Completa *</label>
            <textarea
              id="description"
              v-model="formData.description"
              class="form-control"
              rows="4"
              required></textarea>
          </div>
        </div>

        <div class="form-section">
          <h2>Preço e Categoria</h2>
          <div class="form-group">
            <label for="price">Preço *</label>
            <input
              id="price"
              v-model.number="formData.price"
              type="number"
              step="0.01"
              min="0"
              class="form-control"
              required>
          </div>
          <div class="form-group">
            <label for="promotionalPrice">Preço Promocional</label>
            <input
              id="promotionalPrice"
              v-model.number="formData.promotionalPrice"
              type="number"
              step="0.01"
              min="0"
              class="form-control">
          </div>
          <div class="form-group">
            <label for="categoryId">Categoria *</label>
            <select
              id="categoryId"
              v-model="formData.categoryId"
              class="form-control"
              required>
              <option value="">Selecione uma categoria</option>
              <option value="1">Eletrônicos</option>
              <option value="2">Roupas</option>
              <option value="3">Casa</option>
            </select>
          </div>
          <div class="form-group">
            <label for="brandId">Marca</label>
            <select
              id="brandId"
              v-model="formData.brandId"
              class="form-control">
              <option value="">Selecione uma marca</option>
              <option value="1">Marca A</option>
              <option value="2">Marca B</option>
            </select>
          </div>
        </div>

        <div class="form-section">
          <h2>Dimensões e Peso</h2>
          <div class="form-row">
            <div class="form-group">
              <label for="weight">Peso (kg) *</label>
              <input
                id="weight"
                v-model.number="formData.weight"
                type="number"
                step="0.01"
                min="0"
                class="form-control"
                required>
            </div>
            <div class="form-group">
              <label for="width">Largura (cm) *</label>
              <input
                id="width"
                v-model.number="formData.width"
                type="number"
                step="0.1"
                min="0"
                class="form-control"
                required>
            </div>
            <div class="form-group">
              <label for="height">Altura (cm) *</label>
              <input
                id="height"
                v-model.number="formData.height"
                type="number"
                step="0.1"
                min="0"
                class="form-control"
                required>
            </div>
            <div class="form-group">
              <label for="depth">Profundidade (cm) *</label>
              <input
                id="depth"
                v-model.number="formData.depth"
                type="number"
                step="0.1"
                min="0"
                class="form-control"
                required>
            </div>
          </div>
        </div>

        <div class="form-section">
          <h2>Opções Adicionais</h2>
          <div class="form-group checkbox-group">
            <label>
              <input
                v-model="formData.featured"
                type="checkbox">
              Produto em Destaque
            </label>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Salvando...' : 'Criar Produto' }}
          </button>
          <router-link to="/products" class="btn btn-secondary">
            Cancelar
          </router-link>
        </div>
      </form>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '../../components/layout/Layout.vue'
import { productService, CreateProductRequest } from '../../services/product'

const router = useRouter()
const loading = ref(false)

const formData = ref<CreateProductRequest>({
  name: '',
  sku: '',
  ean: '',
  description: '',
  shortDescription: '',
  price: 0,
  promotionalPrice: undefined,
  categoryId: '',
  brandId: undefined,
  weight: 0,
  width: 0,
  height: 0,
  depth: 0,
  images: [],
  specifications: {},
  featured: false
})

const handleSubmit = async () => {
  loading.value = true
  try {
    await productService.createProduct(formData.value)
    router.push('/products')
  } catch (error) {
    console.error('Error creating product:', error)
    alert('Erro ao criar produto. Tente novamente.')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.product-create-page {
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.product-form {
  max-width: 800px;
}

.form-section {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 1.5rem;
}

.form-section h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  font-size: 1.25rem;
  color: #374151;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #374151;
}

.form-control {
  width: 100%;
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 1rem;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.checkbox-group input[type="checkbox"] {
  width: 1.25rem;
  height: 1.25rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-primary:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.btn-secondary {
  background: #6b7280;
  color: white;
  text-decoration: none;
}

.btn-secondary:hover {
  background: #4b5563;
}
</style>

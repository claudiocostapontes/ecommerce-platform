<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>E-Commerce Admin</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/dashboard" class="nav-link">
          Dashboard
        </router-link>
        <router-link to="/products" class="nav-link">
          Produtos
        </router-link>
        <router-link to="/orders" class="nav-link">
          Pedidos
        </router-link>
        <router-link to="/customers" class="nav-link">
          Clientes
        </router-link>
        <router-link to="/inventory" class="nav-link">
          Estoque
        </router-link>
      </nav>
    </aside>
    <main class="main-content">
      <slot></slot>
    </main>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 250px;
  background: #1f2937;
  color: white;
  padding: 1.5rem;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
}

.sidebar-header {
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #374151;
}

.sidebar-header h2 {
  font-size: 1.25rem;
  margin: 0;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.nav-link {
  color: #d1d5db;
  text-decoration: none;
  padding: 0.75rem 1rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.nav-link:hover {
  background: #374151;
  color: white;
}

.nav-link.router-link-active {
  background: #3b82f6;
  color: white;
}

.main-content {
  flex: 1;
  margin-left: 250px;
  background: #f3f4f6;
}
</style>

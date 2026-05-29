import axios from "axios";

const API = axios.create({
  baseURL: "/api",
});

API.interceptors.request.use((config) => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  if (user.id) {
    config.headers["X-User-Id"] = user.id;
  }
  return config;
});

export const api = {
  // Auth
  login: (data) => API.post("/auth/login", data),
  register: (data) => API.post("/auth/register", data),

  // Products
  getProducts: (params) => API.get("/products", { params }),
  getProduct: (id) => API.get(`/products/${id}`),
  createProduct: (data) => API.post("/products", data),
  updateProduct: (id, data) => API.put(`/products/${id}`, data),
  deleteProduct: (id) => API.delete(`/products/${id}`),
  enableProduct: (id) => API.patch(`/products/${id}/enable`),
  disableProduct: (id) => API.patch(`/products/${id}/disable`),

  // Categories
  getCategories: () => API.get("/categories"),
  createCategory: (data) => API.post("/categories", data),
  updateCategory: (id, data) => API.put(`/categories/${id}`, data),
  deleteCategory: (id) => API.delete(`/categories/${id}`),

  // Cart
  getCart: () => API.get("/cart"),
  addToCart: (productId, quantity = 1) =>
    API.post("/cart", { productId, quantity }),
  updateCartItem: (id, quantity) => API.put(`/cart/items/${id}?quantity=${quantity}`),
  removeFromCart: (id) => API.delete(`/cart/items/${id}`),
  clearCart: () => API.delete("/cart"),

  // Addresses
  getAddresses: () => API.get("/addresses"),
  addAddress: (data) => API.post("/addresses", data),
  updateAddress: (id, data) => API.put(`/addresses/${id}`, data),
  deleteAddress: (id) => API.delete(`/addresses/${id}`),

  // Orders
  checkout: (addressId) => API.post(`/orders/checkout?addressId=${addressId || ''}`),
  getMyOrders: () => API.get("/orders/my"),
  getAllOrders: () => API.get("/orders"),
  updateOrderStatus: (id, status) =>
    API.put(`/orders/${id}/status?status=${status}`),
  deleteOrder: (id) => API.delete(`/orders/${id}`),

  // Admin
  getAllUsers: () => API.get("/admin/users"),
  getUserById: (id) => API.get(`/admin/users/${id}`),
  updateUserRoles: (id, roleIds) => API.put(`/admin/users/${id}/roles`, { roleIds }),
  getInventory: () => API.get("/admin/inventory"),
  updateInventory: (productId, quantity) =>
    API.put(`/admin/inventory/${productId}`, { quantity }),
};

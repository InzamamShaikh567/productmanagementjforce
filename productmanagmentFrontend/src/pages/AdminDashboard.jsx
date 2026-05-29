import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../services/api";
import Navbar from "../components/Navbar";

function AdminDashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const roles = user.roles || [];
  const isSuperAdmin = roles.includes("SUPER_ADMIN");
  const isAdmin = roles.includes("ADMIN") || isSuperAdmin;

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [users, setUsers] = useState([]);
  const [orders, setOrders] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [activeTab, setActiveTab] = useState("products");
  const [showProductForm, setShowProductForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [showCategoryForm, setShowCategoryForm] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);

  const [productForm, setProductForm] = useState({
    name: "", description: "", price: "", categoryId: "", imageUrl: ""
  });

  const [categoryForm, setCategoryForm] = useState({
    name: "", description: ""
  });

  const tabs = ["products", "inventory"];
  if (isSuperAdmin) tabs.push("users", "categories");
  if (isSuperAdmin) tabs.push("orders");

  useEffect(() => {
    if (!user.id || !isAdmin) { navigate("/"); return; }
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [productsRes, ordersRes] = await Promise.all([
        api.getProducts(), isSuperAdmin ? api.getAllOrders() : Promise.resolve({ data: [] })
      ]);
      setProducts(productsRes.data);
      if (isSuperAdmin) {
        const [usersRes, catsRes] = await Promise.all([
          api.getAllUsers(), api.getCategories()
        ]);
        setUsers(usersRes.data);
        setCategories(catsRes.data);
      } else {
        const catsRes = await api.getCategories();
        setCategories(catsRes.data);
      }
      const invRes = await api.getInventory();
      setInventory(invRes.data);
      setOrders(ordersRes.data);
    } catch (err) { console.error(err); }
  };

  const handleProductSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        ...productForm,
        price: parseFloat(productForm.price),
        imageUrl: productForm.imageUrl || `https://placehold.co/150x150?text=${encodeURIComponent(productForm.name)}`
      };
      if (editingProduct) {
        await api.updateProduct(editingProduct.id, data);
      } else {
        await api.createProduct(data);
      }
      resetProductForm();
      loadData();
    } catch (err) { alert("Failed to save product"); }
  };

  const handleEditProduct = (product) => {
    setEditingProduct(product);
    setProductForm({
      name: product.name, description: product.description,
      price: product.price.toString(), categoryId: product.categoryId,
      imageUrl: product.imageUrl
    });
    setShowProductForm(true);
    window.scrollTo(0, 0);
  };

  const handleDeleteProduct = async (id) => {
    if (confirm("Delete this product?")) {
      try { await api.deleteProduct(id); loadData(); }
      catch (err) { alert("Failed to delete product"); }
    }
  };

  const handleToggleProduct = async (id, enabled) => {
    try {
      if (enabled) await api.enableProduct(id);
      else await api.disableProduct(id);
      loadData();
    } catch (err) { alert("Failed to toggle product"); }
  };

  const resetProductForm = () => {
    setShowProductForm(false);
    setEditingProduct(null);
    setProductForm({ name: "", description: "", price: "", categoryId: "", imageUrl: "" });
  };

  const handleCategorySubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCategory) {
        await api.updateCategory(editingCategory.id, categoryForm);
      } else {
        await api.createCategory(categoryForm);
      }
      setShowCategoryForm(false);
      setEditingCategory(null);
      setCategoryForm({ name: "", description: "" });
      loadData();
    } catch (err) { alert("Failed to save category"); }
  };

  const handleEditCategory = (cat) => {
    setEditingCategory(cat);
    setCategoryForm({ name: cat.name, description: cat.description || "" });
    setShowCategoryForm(true);
  };

  const handleDeleteCategory = async (id) => {
    if (confirm("Delete this category?")) {
      try { await api.deleteCategory(id); loadData(); }
      catch (err) { alert(err.response?.data?.message || "Failed to delete category"); }
    }
  };

  const handleUpdateInventory = async (productId, quantity) => {
    try { await api.updateInventory(productId, parseInt(quantity)); loadData(); }
    catch (err) { alert("Failed to update inventory"); }
  };

  const handleUpdateRole = async (userId, roleIds) => {
    try { await api.updateUserRoles(userId, roleIds); loadData(); }
    catch (err) { alert("Failed to update roles"); }
  };

  const handleDeleteOrder = async (orderId) => {
    if (!confirm("Delete this order?")) return;
    try { await api.deleteOrder(orderId); loadData(); }
    catch (err) { alert("Failed to delete order"); }
  };

  const handleUpdateStatus = async (orderId, status) => {
    try { await api.updateOrderStatus(orderId, status); loadData(); }
    catch (err) { alert("Failed to update status"); }
  };

  // User role assignment
  const [selectedUser, setSelectedUser] = useState(null);
  const availableRoles = [
    { id: 1, name: "USER" },
    { id: 2, name: "ADMIN" },
    { id: 3, name: "SUPER_ADMIN" }
  ];

  const handleRoleSelect = (roleId) => {
    if (!selectedUser) return;
    setSelectedUser({ ...selectedUser, roleIds: [roleId] });
  };

  const handleSaveRoles = () => {
    if (!selectedUser) return;
    handleUpdateRole(selectedUser.id, selectedUser.roleIds);
    setSelectedUser(null);
  };

  return (
    <div>
      <Navbar />
      <div style={{ padding: "20px", maxWidth: "1200px", margin: "0 auto" }}>
        <h1>{isSuperAdmin ? "Super Admin Dashboard" : "Admin Dashboard"}</h1>
        <div style={{ display: "flex", gap: "10px", marginBottom: "20px", flexWrap: "wrap" }}>
          {tabs.map(tab => (
            <button key={tab} onClick={() => setActiveTab(tab)} style={{
              padding: "10px 20px", background: activeTab === tab ? "#4a90e2" : "#ddd",
              color: activeTab === tab ? "white" : "#2c3e50", border: "1px solid #ccc",
              borderRadius: "4px", cursor: "pointer"
            }}>{tab.charAt(0).toUpperCase() + tab.slice(1)}</button>
          ))}
        </div>

        {/* Products Tab */}
        {activeTab === "products" && (
          <div>
            <div style={{ marginBottom: "20px" }}>
              <button onClick={() => { resetProductForm(); setShowProductForm(true); }} style={{
                padding: "10px 20px", background: "#2ecc71", color: "white", border: "none", borderRadius: "4px", cursor: "pointer"
              }}>Add Product</button>
            </div>
            {showProductForm && (
              <div style={{ background: "white", padding: "20px", border: "1px solid #ddd", borderRadius: "4px", marginBottom: "20px" }}>
                <h3>{editingProduct ? "Edit Product" : "Add New Product"}</h3>
                <form onSubmit={handleProductSubmit}>
                  <div style={{ marginBottom: "15px" }}>
                    <label>Name:</label>
                    <input type="text" value={productForm.name} onChange={e => setProductForm({...productForm, name: e.target.value})} required style={{ width: "100%", padding: "8px", marginTop: "5px", boxSizing: "border-box" }} />
                  </div>
                  <div style={{ marginBottom: "15px" }}>
                    <label>Description:</label>
                    <textarea value={productForm.description} onChange={e => setProductForm({...productForm, description: e.target.value})} style={{ width: "100%", padding: "8px", marginTop: "5px", boxSizing: "border-box" }} />
                  </div>
                  <div style={{ marginBottom: "15px" }}>
                    <label>Price:</label>
                    <input type="number" step="0.01" value={productForm.price} onChange={e => setProductForm({...productForm, price: e.target.value})} required style={{ width: "100%", padding: "8px", marginTop: "5px", boxSizing: "border-box" }} />
                  </div>
                  <div style={{ marginBottom: "15px" }}>
                    <label>Category:</label>
                    <select value={productForm.categoryId} onChange={e => setProductForm({...productForm, categoryId: e.target.value})} required style={{ width: "100%", padding: "8px", marginTop: "5px", boxSizing: "border-box" }}>
                      <option value="">Select category</option>
                      {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                    </select>
                  </div>
                  <div style={{ marginBottom: "15px" }}>
                    <label>Image URL (leave empty for default):</label>
                    <input type="text" value={productForm.imageUrl} onChange={e => setProductForm({...productForm, imageUrl: e.target.value})} style={{ width: "100%", padding: "8px", marginTop: "5px", boxSizing: "border-box" }} />
                  </div>
                  <div style={{ display: "flex", gap: "10px" }}>
                    <button type="submit" style={{ padding: "10px 20px", background: "#4a90e2", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Save</button>
                    <button type="button" onClick={resetProductForm} style={{ padding: "10px 20px", background: "#ddd", color: "#2c3e50", border: "1px solid #ccc", borderRadius: "4px", cursor: "pointer" }}>Cancel</button>
                  </div>
                </form>
              </div>
            )}
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr style={{ background: "#f8f9fa" }}>
                  <th style={{ padding: "12px", textAlign: "left" }}>Name</th>
                  <th style={{ padding: "12px", textAlign: "left" }}>Category</th>
                  <th style={{ padding: "12px", textAlign: "left" }}>Price</th>
                  <th style={{ padding: "12px", textAlign: "left" }}>Status</th>
                  <th style={{ padding: "12px", textAlign: "left" }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {products.map((p) => (
                  <tr key={p.id} style={{ borderBottom: "1px solid #ddd" }}>
                    <td style={{ padding: "12px" }}>{p.name}</td>
                    <td style={{ padding: "12px" }}>{p.categoryName}</td>
                    <td style={{ padding: "12px" }}>${Number(p.price).toFixed(2)}</td>
                    <td style={{ padding: "12px" }}>
                      <span style={{ color: p.enabled ? "#2ecc71" : "#e74c3c", fontWeight: "bold" }}>
                        {p.enabled ? "Enabled" : "Disabled"}
                      </span>
                    </td>
                    <td style={{ padding: "12px" }}>
                      <button onClick={() => handleEditProduct(p)} style={{ padding: "5px 10px", background: "#4a90e2", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", marginRight: "5px" }}>Edit</button>
                      <button onClick={() => handleToggleProduct(p.id, !p.enabled)} style={{ padding: "5px 10px", background: p.enabled ? "#e67e22" : "#2ecc71", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", marginRight: "5px" }}>{p.enabled ? "Disable" : "Enable"}</button>
                      <button onClick={() => handleDeleteProduct(p.id)} style={{ padding: "5px 10px", background: "#e74c3c", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Inventory Tab */}
        {activeTab === "inventory" && (
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f8f9fa" }}>
                <th style={{ padding: "12px", textAlign: "left" }}>Product</th>
                <th style={{ padding: "12px", textAlign: "left" }}>Current Stock</th>
                <th style={{ padding: "12px", textAlign: "left" }}>Update</th>
              </tr>
            </thead>
            <tbody>
              {inventory.map((inv) => (
                <tr key={inv.productId} style={{ borderBottom: "1px solid #ddd" }}>
                  <td style={{ padding: "12px" }}>{inv.productName}</td>
                  <td style={{ padding: "12px" }}>{inv.quantity}</td>
                  <td style={{ padding: "12px" }}>
                    <input type="number" id={`inv-${inv.productId}`} defaultValue={inv.quantity}
                      style={{ width: "80px", padding: "5px", marginRight: "10px" }} />
                    <button onClick={() => {
                      const val = document.getElementById(`inv-${inv.productId}`).value;
                      handleUpdateInventory(inv.productId, val);
                    }} style={{ padding: "5px 10px", background: "#4a90e2", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Update</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {/* Users Tab */}
        {activeTab === "users" && isSuperAdmin && (
          <div style={{ display: "flex", gap: "20px" }}>
            <div style={{ flex: 1 }}>
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                  <tr style={{ background: "#f8f9fa" }}>
                    <th style={{ padding: "12px", textAlign: "left" }}>ID</th>
                    <th style={{ padding: "12px", textAlign: "left" }}>Username</th>
                    <th style={{ padding: "12px", textAlign: "left" }}>Email</th>
                    <th style={{ padding: "12px", textAlign: "left" }}>Roles</th>
                    <th style={{ padding: "12px", textAlign: "left" }}></th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.id} style={{ borderBottom: "1px solid #ddd" }}>
                      <td style={{ padding: "12px" }}>{u.id}</td>
                      <td style={{ padding: "12px" }}>{u.username}</td>
                      <td style={{ padding: "12px" }}>{u.email}</td>
                      <td style={{ padding: "12px" }}>{(u.roles || []).join(", ")}</td>
                      <td style={{ padding: "12px" }}>
                        <button onClick={() => {
                          const currentRoleIds = (u.roles || []).map(name => {
                            const role = availableRoles.find(r => r.name === name);
                            return role ? role.id : null;
                          }).filter(id => id !== null);
                          setSelectedUser({ ...u, roleIds: currentRoleIds });
                        }} style={{ padding: "5px 10px", background: "#4a90e2", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Edit Roles</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {selectedUser && (
              <div style={{ width: "300px", background: "white", padding: "20px", border: "1px solid #ddd", borderRadius: "4px" }}>
                <h3>Edit Roles: {selectedUser.username}</h3>
                {availableRoles.map(role => (
                  <label key={role.id} style={{ display: "block", marginBottom: "10px", cursor: "pointer" }}>
                    <input type="radio" name="user-role" checked={((selectedUser.roleIds || [])[0] || 0) === role.id} onChange={() => handleRoleSelect(role.id)} style={{ marginRight: "8px" }} />
                    {role.name}
                  </label>
                ))}
                <div style={{ display: "flex", gap: "10px", marginTop: "15px" }}>
                  <button onClick={handleSaveRoles} style={{ padding: "8px 16px", background: "#4a90e2", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Save</button>
                  <button onClick={() => setSelectedUser(null)} style={{ padding: "8px 16px", background: "#ddd", color: "#2c3e50", border: "1px solid #ccc", borderRadius: "4px", cursor: "pointer" }}>Cancel</button>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Categories Tab */}
        {activeTab === "categories" && isSuperAdmin && (
          <div>
            <div style={{ marginBottom: "20px" }}>
              <button onClick={() => { setShowCategoryForm(true); setEditingCategory(null); setCategoryForm({ name: "", description: "" }); }} style={{
                padding: "10px 20px", background: "#2ecc71", color: "white", border: "none", borderRadius: "4px", cursor: "pointer"
              }}>Add Category</button>
            </div>
            {showCategoryForm && (
              <div style={{ background: "white", padding: "20px", border: "1px solid #ddd", borderRadius: "4px", marginBottom: "20px" }}>
                <h3>{editingCategory ? "Edit Category" : "Add New Category"}</h3>
                <form onSubmit={handleCategorySubmit}>
                  <div style={{ marginBottom: "15px" }}>
                    <label>Name:</label>
                    <input type="text" value={categoryForm.name} onChange={e => setCategoryForm({...categoryForm, name: e.target.value})} required style={{ width: "100%", padding: "8px", marginTop: "5px", boxSizing: "border-box" }} />
                  </div>
                  <div style={{ marginBottom: "15px" }}>
                    <label>Description:</label>
                    <textarea value={categoryForm.description} onChange={e => setCategoryForm({...categoryForm, description: e.target.value})} style={{ width: "100%", padding: "8px", marginTop: "5px", boxSizing: "border-box" }} />
                  </div>
                  <div style={{ display: "flex", gap: "10px" }}>
                    <button type="submit" style={{ padding: "10px 20px", background: "#4a90e2", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Save</button>
                    <button type="button" onClick={() => { setShowCategoryForm(false); setEditingCategory(null); }} style={{ padding: "10px 20px", background: "#ddd", color: "#2c3e50", border: "1px solid #ccc", borderRadius: "4px", cursor: "pointer" }}>Cancel</button>
                  </div>
                </form>
              </div>
            )}
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr style={{ background: "#f8f9fa" }}>
                  <th style={{ padding: "12px", textAlign: "left" }}>Name</th>
                  <th style={{ padding: "12px", textAlign: "left" }}>Description</th>
                  <th style={{ padding: "12px", textAlign: "left" }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {categories.map((c) => (
                  <tr key={c.id} style={{ borderBottom: "1px solid #ddd" }}>
                    <td style={{ padding: "12px" }}>{c.name}</td>
                    <td style={{ padding: "12px" }}>{c.description}</td>
                    <td style={{ padding: "12px" }}>
                      <button onClick={() => handleEditCategory(c)} style={{ padding: "5px 10px", background: "#4a90e2", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", marginRight: "5px" }}>Edit</button>
                      <button onClick={() => handleDeleteCategory(c.id)} style={{ padding: "5px 10px", background: "#e74c3c", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Orders Tab */}
        {activeTab === "orders" && (
          <div>
            {orders.map((order) => (
              <div key={order.id} style={{
                border: "1px solid #ddd", borderRadius: "4px", padding: "15px", marginBottom: "15px", background: "white"
              }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" }}>
                    <div>
                      <strong>Order #{order.id}</strong> by <strong>{order.username}</strong> - {order.items.map(i => i.productName).join(", ")}
                    </div>
                  <div style={{ display: "flex", gap: "8px", alignItems: "center" }}>
                    <select value={order.status} onChange={e => handleUpdateStatus(order.id, e.target.value)} style={{ padding: "5px" }}>
                      <option value="PROCESSING">Processing</option>
                      <option value="SHIPPED">Shipped</option>
                      <option value="OUT_FOR_DELIVERY">Out for Delivery</option>
                      <option value="DELIVERED">Delivered</option>
                      <option value="CANCELED">Canceled</option>
                    </select>
                    {isSuperAdmin && (
                      <button onClick={() => handleDeleteOrder(order.id)} style={{ padding: "5px 10px", background: "#e74c3c", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Delete</button>
                    )}
                  </div>
                </div>
                <div style={{ fontSize: "14px", color: "#666" }}>
                  Total: ${Number(order.totalAmount).toFixed(2)} | Date: {new Date(order.orderDate).toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" })}
                  {order.address && ` | Ship to: ${order.address.street}, ${order.address.city}`}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default AdminDashboard;

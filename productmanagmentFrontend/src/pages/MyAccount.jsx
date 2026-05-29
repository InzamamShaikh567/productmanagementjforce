import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../services/api";
import Navbar from "../components/Navbar";

function MyAccount() {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [cart, setCart] = useState([]);
  const [addresses, setAddresses] = useState([]);
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [addressForm, setAddressForm] = useState({
    street: "", city: "", state: "", zipCode: "", country: "", isDefault: false
  });

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.id) {
      navigate("/");
      return;
    }
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [ordersRes, cartRes, addrRes] = await Promise.all([
        api.getMyOrders(),
        api.getCart(),
        api.getAddresses()
      ]);
      setOrders(ordersRes.data);
      setCart(cartRes.data.items || []);
      setAddresses(addrRes.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleRemove = async (id) => {
    try {
      await api.removeFromCart(id);
      loadData();
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddressSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.addAddress(addressForm);
      setShowAddressForm(false);
      setAddressForm({ street: "", city: "", state: "", zipCode: "", country: "", isDefault: false });
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to add address");
    }
  };

  const handleDeleteAddress = async (id) => {
    try {
      await api.deleteAddress(id);
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to delete address");
    }
  };

  const total = cart.reduce(
    (sum, item) => sum + item.productPrice * item.quantity, 0
  );

  const handleCheckout = () => {
    navigate("/checkout");
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "DELIVERED": return "#2ecc71";
      case "CANCELED": return "#e74c3c";
      default: return "#f39c12";
    }
  };

  return (
    <div>
      <Navbar />
      <div style={{ padding: "20px", maxWidth: "720px", margin: "0 auto" }}>
        <h1>My Account</h1>

        {/* Addresses */}
        <div style={{ marginBottom: "30px" }}>
          <h3 style={{ borderBottom: "1px solid #ddd", paddingBottom: "10px" }}>
            My Addresses
          </h3>
          {addresses.length === 0 ? (
            <p style={{ color: "#888" }}>No addresses saved</p>
          ) : (
            addresses.map((addr) => (
              <div key={addr.id} style={{
                border: "1px solid #ddd", padding: "10px", marginBottom: "10px",
                background: "white", borderRadius: "4px"
              }}>
                <p style={{ margin: 0 }}>{addr.street}, {addr.city}, {addr.state} {addr.zipCode}, {addr.country}</p>
                <div style={{ display: "flex", gap: "10px", marginTop: "5px", alignItems: "center" }}>
                  {addr.isDefault && <span style={{ fontSize: "12px", color: "#4a90e2", fontWeight: "bold" }}>DEFAULT</span>}
                  <button onClick={() => handleDeleteAddress(addr.id)} style={{
                    padding: "4px 10px", background: "#e74c3c", color: "white",
                    border: "none", borderRadius: "4px", cursor: "pointer", fontSize: "12px"
                  }}>Delete</button>
                </div>
              </div>
            ))
          )}
          {showAddressForm ? (
            <form onSubmit={handleAddressSubmit} style={{
              background: "white", padding: "15px", border: "1px solid #ddd", borderRadius: "4px", marginTop: "10px"
            }}>
              <div style={{ marginBottom: "10px" }}>
                <input type="text" placeholder="Street" value={addressForm.street}
                  onChange={(e) => setAddressForm({...addressForm, street: e.target.value})} required
                  style={{ width: "100%", padding: "8px", boxSizing: "border-box" }} />
              </div>
              <div style={{ display: "flex", gap: "10px", marginBottom: "10px" }}>
                <input type="text" placeholder="City" value={addressForm.city}
                  onChange={(e) => setAddressForm({...addressForm, city: e.target.value})} required
                  style={{ flex: 1, padding: "8px" }} />
                <input type="text" placeholder="State" value={addressForm.state}
                  onChange={(e) => setAddressForm({...addressForm, state: e.target.value})} required
                  style={{ flex: 1, padding: "8px" }} />
              </div>
              <div style={{ display: "flex", gap: "10px", marginBottom: "10px" }}>
                <input type="text" placeholder="Zip Code" value={addressForm.zipCode}
                  onChange={(e) => setAddressForm({...addressForm, zipCode: e.target.value})} required
                  style={{ flex: 1, padding: "8px" }} />
                <input type="text" placeholder="Country" value={addressForm.country}
                  onChange={(e) => setAddressForm({...addressForm, country: e.target.value})} required
                  style={{ flex: 1, padding: "8px" }} />
              </div>
              <label style={{ display: "flex", alignItems: "center", gap: "5px", marginBottom: "10px" }}>
                <input type="checkbox" checked={addressForm.isDefault}
                  onChange={(e) => setAddressForm({...addressForm, isDefault: e.target.checked})} />
                Set as default
              </label>
              <div style={{ display: "flex", gap: "10px" }}>
                <button type="submit" style={{ padding: "8px 16px", background: "#4a90e2", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}>Save</button>
                <button type="button" onClick={() => setShowAddressForm(false)} style={{ padding: "8px 16px", background: "#ddd", color: "#2c3e50", border: "1px solid #ccc", borderRadius: "4px", cursor: "pointer" }}>Cancel</button>
              </div>
            </form>
          ) : (
            <button onClick={() => setShowAddressForm(true)} style={{
              padding: "8px 16px", background: "#2ecc71", color: "white",
              border: "none", borderRadius: "4px", cursor: "pointer", marginTop: "10px"
            }}>+ Add Address</button>
          )}
        </div>

        {/* Orders */}
        <div style={{ marginBottom: "40px" }}>
          <h3 style={{ borderBottom: "1px solid #ddd", paddingBottom: "10px" }}>
            My Orders
          </h3>
          {orders.length === 0 ? (
            <p style={{ color: "#888" }}>No orders yet</p>
          ) : (
            orders.map((order) => (
              <div key={order.id} style={{ borderBottom: "1px solid #ddd", padding: "15px 0" }}>
                {order.items.map((item) => (
                  <div key={item.id} style={{ display: "flex", alignItems: "center", gap: "15px" }}>
                    <img src={item.imageUrl} alt={item.productName}
                      style={{ width: "60px", height: "60px", objectFit: "cover" }} />
                    <div style={{ flex: 1 }}>
                      <p style={{ margin: 0, fontWeight: "bold" }}>{item.productName}</p>
                      <p style={{ margin: "5px 0 0", color: "#666", fontSize: "14px" }}>
                        Order #{order.id} - {order.orderDate} - Qty: {item.quantity}
                      </p>
                    </div>
                    <span style={{ color: getStatusColor(order.status), fontSize: "14px", fontWeight: "bold" }}>
                      {order.status.replace("_", " ")}
                    </span>
                  </div>
                ))}
              </div>
            ))
          )}
        </div>

        {/* Cart */}
        <div>
          <h3 style={{ borderBottom: "1px solid #ddd", paddingBottom: "10px" }}>
            My Cart
          </h3>
          {cart.length === 0 ? (
            <p style={{ color: "#888" }}>Cart is empty</p>
          ) : (
            <div>
              {cart.map((item) => (
                <div key={item.id} style={{
                  display: "flex", alignItems: "center", gap: "15px",
                  borderBottom: "1px solid #ddd", padding: "15px 0"
                }}>
                  <img src={item.imageUrl} alt={item.productName}
                    style={{ width: "60px", height: "60px", objectFit: "cover" }} />
                  <div style={{ flex: 1 }}>
                    <p style={{ margin: 0, fontWeight: "bold" }}>{item.productName}</p>
                    <p style={{ margin: "5px 0 0", color: "#666" }}>${Number(item.productPrice).toFixed(2)} x {item.quantity}</p>
                  </div>
                  <button onClick={() => handleRemove(item.id)} style={{
                    padding: "8px 16px", background: "#ddd", color: "#2c3e50",
                    border: "1px solid #ccc", borderRadius: "4px", cursor: "pointer"
                  }}>Remove</button>
                </div>
              ))}
              <div style={{ marginTop: "20px", textAlign: "right" }}>
                <p style={{ fontSize: "20px", fontWeight: "bold" }}>
                  Total: ${total.toFixed(2)}
                </p>
                <button onClick={handleCheckout} style={{
                  padding: "12px 24px", background: "#4a90e2", color: "white",
                  border: "none", borderRadius: "4px", cursor: "pointer", fontSize: "16px"
                }}>Proceed to Checkout</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default MyAccount;

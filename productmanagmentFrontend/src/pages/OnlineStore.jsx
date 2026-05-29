import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../services/api";
import ProductCard from "../components/ProductCard";
import Navbar from "../components/Navbar";

function OnlineStore() {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [search, setSearch] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");

  useEffect(() => {
    loadCategories();
  }, []);

  useEffect(() => {
    loadProducts();
  }, [selectedCategory]);

  const loadCategories = async () => {
    try {
      const res = await api.getCategories();
      setCategories(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const loadProducts = async () => {
    try {
      const params = {};
      if (selectedCategory) params.category = selectedCategory;
      if (search) params.search = search;
      const res = await api.getProducts(params);
      setProducts(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    loadProducts();
  };

  const handleAddToCart = async (productId) => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.id) {
      navigate("/");
      return;
    }
    try {
      await api.addToCart(productId);
      alert("Added to cart!");
    } catch (err) {
      alert(err.response?.data?.message || "Failed to add to cart");
    }
  };

  return (
    <div>
      <Navbar />
      <div style={{ padding: "20px", maxWidth: "1200px", margin: "0 auto" }}>
        <div
          style={{
            borderBottom: "1px solid #ddd",
            paddingBottom: "10px",
            marginBottom: "20px",
          }}
        >
          <h1 style={{ margin: 0, color: "#2c3e50" }}>
            Welcome to Our Online Store
          </h1>
        </div>

        <form
          onSubmit={handleSearch}
          style={{ textAlign: "center", marginBottom: "20px" }}
        >
          <input
            type="text"
            placeholder="Search for products..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{
              width: "100%",
              maxWidth: "500px",
              padding: "12px",
              border: "1px solid #ddd",
              borderRadius: "4px",
              fontSize: "16px",
            }}
          />
        </form>

        <div
          style={{
            background: "#4a90e2",
            color: "white",
            textAlign: "center",
            padding: "20px",
            marginBottom: "20px",
            borderRadius: "4px",
          }}
        >
          <strong>Big Sale! Up to 50% off on selected items!</strong>
        </div>

        <div style={{ textAlign: "center", marginBottom: "10px" }}>
          <button
            onClick={() => setSelectedCategory("")}
            style={{
              padding: "12px 24px",
              background: selectedCategory === "" ? "#4a90e2" : "#ddd",
              color: selectedCategory === "" ? "white" : "#2c3e50",
              border: "1px solid #ccc",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            All
          </button>
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(4, 1fr)",
            gap: "10px",
            marginBottom: "30px",
          }}
        >
          {categories.map((cat) => (
            <button
              key={cat.id}
              onClick={() => setSelectedCategory(cat.id.toString())}
              style={{
                padding: "12px 24px",
                background: selectedCategory === cat.id.toString() ? "#4a90e2" : "#ddd",
                color: selectedCategory === cat.id.toString() ? "white" : "#2c3e50",
                border: "1px solid #ccc",
                borderRadius: "4px",
                cursor: "pointer",
              }}
            >
              {cat.name}
            </button>
          ))}
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(3, 1fr)",
            gap: "30px",
          }}
        >
          {products.map((product) => (
            <ProductCard key={product.id} product={product} onAddToCart={handleAddToCart} />
          ))}
        </div>

        <div
          style={{
            marginTop: "40px",
            borderTop: "1px solid #ddd",
            paddingTop: "20px",
            textAlign: "center",
            color: "#888",
          }}
        >
          &copy; 2026 Online Store by Inzamam.
        </div>
      </div>
    </div>
  );
}

export default OnlineStore;

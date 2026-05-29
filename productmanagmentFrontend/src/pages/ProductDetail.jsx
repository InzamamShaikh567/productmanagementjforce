import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { api } from "../services/api";
import Navbar from "../components/Navbar";

function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);

  useEffect(() => {
    loadProduct();
  }, [id]);

  const loadProduct = async () => {
    try {
      const res = await api.getProduct(id);
      setProduct(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddToCart = async () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.id) {
      navigate("/");
      return;
    }
    try {
      await api.addToCart(product.id);
      alert("Added to cart!");
    } catch (err) {
      alert(err.response?.data?.message || "Failed to add to cart");
    }
  };

  const handleBuyNow = async () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.id) {
      navigate("/");
      return;
    }
    try {
      await api.addToCart(product.id);
      navigate("/account");
    } catch (err) {
      alert(err.response?.data?.message || "Failed to add to cart");
    }
  };

  if (!product) return <div>Loading...</div>;

  return (
    <div>
      <Navbar />
      <div style={{ padding: "20px", maxWidth: "1000px", margin: "0 auto" }}>
        <div style={{ display: "flex", gap: "40px", marginBottom: "30px" }}>
          <div style={{ flex: "1" }}>
            <img
              src={product.imageUrl}
              alt={product.name}
              style={{ width: "300px", height: "300px", objectFit: "cover" }}
            />
          </div>
          <div style={{ flex: "1.5" }}>
            <h1 style={{ marginTop: 0 }}>{product.name}</h1>
            <p style={{ color: "#666" }}>{product.description}</p>
            <p style={{ fontSize: "24px", fontWeight: "bold" }}>
              ${Number(product.price).toFixed(2)}
            </p>
            <div style={{ display: "flex", gap: "10px" }}>
              <button
                onClick={handleAddToCart}
                style={{
                  padding: "10px 20px",
                  background: "#4a90e2",
                  color: "white",
                  border: "1px solid #ccc",
                  borderRadius: "4px",
                  cursor: "pointer",
                }}
              >
                Add to Cart
              </button>
              <button
                onClick={handleBuyNow}
                style={{
                  padding: "10px 20px",
                  background: "#2ecc71",
                  color: "white",
                  border: "1px solid #ccc",
                  borderRadius: "4px",
                  cursor: "pointer",
                }}
              >
                Buy Now
              </button>
            </div>
          </div>
        </div>
        <hr style={{ border: "none", borderTop: "1px solid #ddd", margin: "30px 0" }} />
        <div>
          <h2>Product Description</h2>
          <p style={{ color: "#555", lineHeight: "1.8" }}>
            {product.description}
          </p>
        </div>
      </div>
    </div>
  );
}

export default ProductDetail;

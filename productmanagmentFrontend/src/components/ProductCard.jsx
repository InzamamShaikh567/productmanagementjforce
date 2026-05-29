import { Link } from "react-router-dom";

function ProductCard({ product, onAddToCart }) {
  const handleAddToCart = (e) => {
    e.preventDefault();
    e.stopPropagation();
    onAddToCart(product.id);
  };

  return (
    <Link
      to={`/product/${product.id}`}
      style={{ textDecoration: "none", color: "inherit" }}
    >
      <div
        style={{
          border: "1px solid #ddd",
          padding: "15px",
          textAlign: "center",
          background: "white",
          cursor: "pointer",
        }}
      >
        <img
          src={product.imageUrl}
          alt={product.name}
          style={{ width: "100px", height: "100px", objectFit: "cover" }}
        />
        <h3 style={{ fontSize: "16px", margin: "10px 0" }}>{product.name}</h3>
        <p style={{ color: "#666", fontSize: "14px" }}>{product.description}</p>
        <p style={{ fontWeight: "bold", fontSize: "18px" }}>${Number(product.price).toFixed(2)}</p>
        <button
          onClick={handleAddToCart}
          style={{
            width: "100%",
            padding: "8px",
            background: "#4a90e2",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Add to Cart
        </button>
      </div>
    </Link>
  );
}

export default ProductCard;

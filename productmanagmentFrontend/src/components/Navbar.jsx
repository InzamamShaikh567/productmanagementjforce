import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const roles = user.roles || [];

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  return (
    <nav
      style={{
        background: "#4a90e2",
        padding: "12px 20px",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        color: "white",
      }}
    >
      <Link
        to="/store"
        style={{
          color: "white",
          textDecoration: "none",
          fontSize: "18px",
          fontWeight: "bold",
        }}
      >
        Online Store
      </Link>
      <div style={{ display: "flex", gap: "15px", alignItems: "center" }}>
        <Link to="/account" style={{ color: "white", textDecoration: "none" }}>
          My Account
        </Link>
        {(roles.includes("ADMIN") || roles.includes("SUPER_ADMIN")) && (
          <Link to="/admin" style={{ color: "white", textDecoration: "none" }}>
            Admin
          </Link>
        )}
        <button
          onClick={handleLogout}
          style={{
            padding: "6px 14px",
            background: "#e74c3c",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;

const CONFIG = {
  API_URL:
    window.location.hostname === "localhost" ||
    window.location.hostname === "127.0.0.1"
      ? "http://localhost:8080/api"
      : `http://${window.location.hostname}:8080/api`,
};

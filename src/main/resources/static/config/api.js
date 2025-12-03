const CONFIG = {
  API_URL:
    window.location.hostname === "localhost"
      ? "http://localhost:8080/api"
      : "http://ec2-18-118-29-99.us-east-2.compute.amazonaws.com:8080/api",
};

export default CONFIG;

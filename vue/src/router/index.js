import { createRouter, createWebHistory } from "vue-router";
import Dashboard from "../Dashboard.vue";
import Home from "../views/Home.vue";
import About from "../views/About.vue";
import Login from "../views/Login.vue";
import Gamelist from "@/views/Gamelist.vue";

const routes = [
  { path: "/", name: "Home", component: Home },
  { path: "/about", name: "About", component: About },
  { path: "/login", name: "Login", component: Login },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: Dashboard,
    children: [{ path: "gamelist", name: "Gamelist", component: Gamelist }],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

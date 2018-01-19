import React from "react";
import { Route } from "react-router-dom";

//引入pages组件
import App from "./App";
import Home from "./pages/Home";
import About from "./pages/About";
import Login from "./pages/Login";
import Pricing from "./pages/Pricing";
import Blog from "./pages/Blog";
import Studio from "./pages/Studio";
//定义路由
const routes = (
  <Route path="/" component={App}>
    <Route path="/" component={Home} />
    <Route path="/about" component={About} />
    <Route path="/join" component={Login} />
    <Route path="/pricing" component={Pricing} />
    <Route path="/blog" component={Blog} />
    <Route path="/studio" component={Studio} />
  </Route>
);

export { routes };

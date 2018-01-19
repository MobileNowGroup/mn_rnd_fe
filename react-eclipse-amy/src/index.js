import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import { BrowserRouter } from "react-router-dom";
import registerServiceWorker from "./registerServiceWorker";
//引入路由
import { routes } from "./router";

ReactDOM.render(
  <BrowserRouter>{routes}</BrowserRouter>,
  document.getElementById("root")
);
registerServiceWorker();

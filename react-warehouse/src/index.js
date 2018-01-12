import React from "react";
import ReactDOM from "react-dom";
import App from "./App";
import registerServiceWorker from "./registerServiceWorker";
import { Provider } from "react-redux";
import { createStore, applyMiddleware } from "redux";
import reducers from "./js/reducers";
import logger from "redux-logger";

ReactDOM.render(
  <Provider store={createStore(reducers, applyMiddleware(logger))}>
    <App />
  </Provider>,
  document.getElementById("root")
);
registerServiceWorker();

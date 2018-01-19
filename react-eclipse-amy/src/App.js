import React, { Component } from "react";
import { Route, Switch } from "react-router-dom";

//引入pages组件
import Home from "./pages/Home";
import About from "./pages/About";
import Login from "./pages/Login";
import Pricing from "./pages/Pricing";
import Blog from "./pages/Blog";
import Studio from "./pages/Studio";

//引入 Header和Footer
import Header from "./components/Header";
import Footer from "./components/Footer";

class App extends Component {
  render() {
    return (
      <div>
        <Header />
        <Switch>
          <Route exact path="/" component={Home} />
          <Route path="/about" component={About} />
          <Route path="/join" component={Login} />
          <Route path="/pricing" component={Pricing} />
          <Route path="/blog" component={Blog} />
          <Route path="/studio" component={Studio} />
        </Switch>
        <Footer />
      </div>
    );
  }
}

export default App;

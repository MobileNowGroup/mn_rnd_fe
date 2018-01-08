import React, {Component} from "react";
import "./App.css";
import "./css/normalize.css";
import "./css/reset.css";
import Header from "./components/app/Header";
import Footer from "./components/app/Footer";
import Home from "./containers/Home";
import About from "./containers/About";
import Studio from "./containers/Studio";
import Pricing from "./containers/Pricing";
import Blog from "./containers/Blog";
import {BrowserRouter as Router, Route} from 'react-router-dom';

class App extends Component {
  render() {
    return (
      <Router>
        <div className="App">
          <Header/>
          <main>
            <Route exact path="/" component={Home}/>
            <Route exact path="/about" component={About}/>
            <Route exact path="/studio" component={Studio}/>
            <Route exact path="/pricing" component={Pricing}/>
            <Route exact path="/blog" component={Blog}/>
          </main>
          <Footer/>
        </div>
      </Router>
    );
  }
}

export default App;

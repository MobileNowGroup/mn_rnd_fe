import React, { Component } from "react";
import Header from "../components/Header";
import Home from "./Home";
import Footer from "../components/Footer";

export default class Index extends Component {
  render() {
    return (
      <div>
        <Header />
        <div>{this.props.children || <Home />} </div>
        <Footer />
      </div>
    );
  }
}

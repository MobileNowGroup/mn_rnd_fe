import React, { Component } from "react";
import Section1 from "../components/Section1";
import Section2 from "../components/Section2";
import Section3 from "../components/Section3";
import Section4 from "../components/Section4";
import Section5 from "../components/Section5";
import Section6 from "../components/Section6";
import "../styles/css/main.css";

class Home extends Component {
  render() {
    return (
      <div className="main">
        <div className="main__contact">
          <label className="main__contact-text">Contact Us</label>
        </div>
        <Section1 />
        <Section2 />
        <Section3 />
        <Section4 />
        <Section5 />
        <Section6 />
      </div>
    );
  }
}

export default Home;

import React, {Component} from 'react';
import Feature from "./Feature";
import "../../css/Section3.css";
import cart from "../../images/feature-cart.svg";
import mail from "../../images/feature-mail.svg";
import direction from "../../images/feature-direction.svg";
import window from "../../images/feature-window.svg";

class Section3 extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div className="section3-container">
      <h2 className="section3-title">FEATURES OF APP</h2>
      <p className="section3-content">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
      </p>
      <div className="features-container">
        <Feature image={cart}/>
        <Feature image={mail}/>
        <Feature image={direction}/>
        <Feature image={window}/>
      </div>
    </div>);
  }
}

export default Section3;

import React, {Component} from 'react';
import "../../css/Section2.css";
import iphone from "../../images/iphone.png";
import iphone2x from "../../images/iphone@2x.png";
import iphone3x from "../../images/iphone@3x.png";

class Section2 extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div className="section2-container">
      <h2 className="section2-title">LATEST DESIGN STYE</h2>
      <p className="section2-content">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
      </p>
      <img src={iphone} srcset={`${iphone2x} 2x, ${iphone3x} 3x`} className="iphone"/>
    </div>);
  }
}

export default Section2;

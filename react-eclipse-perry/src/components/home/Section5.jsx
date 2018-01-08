import React, {Component} from 'react';
import '../../css/Section5.css';
import icon from "../../images/icon.svg";

class Section5 extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div className="section5-containner">
      <div className="section5-content-containner">
        <img className="section5-img" src={icon} alt=""/>
        <div className="section5-text-containner">
          <h3 className="section5-title">Push yourself & your designs</h3>
          <p className="section5-text">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
          </p>
        </div>
      </div>
    </div>);
  }
}

export default Section5;

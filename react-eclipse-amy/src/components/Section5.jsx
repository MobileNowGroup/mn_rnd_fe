import React, { Component } from "react";
import "../styles/css/main.css";

class Section5 extends Component {
  render() {
    return (
      <div className="section5-container">
        <img
          src={require("../images/top_arrow.png")}
          srcSet={`${require("../images/top_arrow@2x.png")} 2x, ${require("../images/top_arrow@3x.png")}3x`}
          className="section5-container__icon"
          alt=""
        />
        <div className="section5-container__content">
          <label className="section5-container__title">
            Push yourself <br /> & your <br />designs
          </label>
          <label className="section5-container__describe">
            Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. <br />
          </label>
        </div>
        <img
          src={require("../images/computer-img.png")}
          srcSet={`${require("../images/computer-img@2x.png")} 2x, ${require("../images/computer-img@3x.png")}3x`}
          alt=""
          className="section5-container__computer-img"
        />
      </div>
    );
  }
}

export default Section5;

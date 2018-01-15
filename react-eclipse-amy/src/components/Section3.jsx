import React, { Component } from "react";
import "../styles/css/main.css";
import { SectionTitle } from "../const/SectionTitle";

const Feature = ({ iconUrl, iconSrcset, title, content }) => {
  return (
    <div className="feature">
      <img src={iconUrl} className="feature__icon" alt="" srcSet={iconSrcset} />
      <label className="feature__title">{title}</label>
      <label className="feature__content"> {content} </label>
    </div>
  );
};

class Section3 extends Component {
  render() {
    return (
      <div className="section3">
        <SectionTitle
          title="FEATURES OF APP"
          describe="Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
        />
        <div className="section3__features">
          <Feature
            id="Lorem ipsum dolor sit"
            iconUrl={require("../images/feature-1.png")}
            iconSrcset={`${require("../images/feature-1@2x.png")} 2x, ${require("../images/feature-1@3x.png")}3x`}
            title="Lorem ipsum dolor sit"
            content="Lorem ipsum dolor sit amet,&#13; consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim"
          />
          <Feature
            id="ametconsectetur"
            iconUrl={require("../images/feature-2.png")}
            iconSrcset={`${require("../images/feature-2@2x.png")} 2x, ${require("../images/feature-2@3x.png")}3x`}
            title="ametconsectetur"
            content="Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna."
          />
          <Feature
            id="Lorem ipsum dolor sit2"
            iconUrl={require("../images/feature-3.png")}
            iconSrcset={`${require("../images/feature-3@2x.png")} 2x, ${require("../images/feature-3@3x.png")}3x`}
            title="Lorem ipsum dolor sit"
            content="Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore."
          />
          <Feature
            id="Lorem ipsum dolor sit3"
            iconUrl={require("../images/feature-4.png")}
            iconSrcset={`${require("../images/feature-4@2x.png")} 2x, ${require("../images/feature-4@3x.png")}3x`}
            title="Lorem ipsum dolor sit"
            content="Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
          />
        </div>
      </div>
    );
  }
}

export default Section3;

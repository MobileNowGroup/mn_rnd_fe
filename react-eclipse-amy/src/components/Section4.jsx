import React, { Component } from "react";
import "../styles/css/main.css";
import { SectionTitle } from "../const/SectionTitle";
import PropTypes from "prop-types";

const Guideline = ({ imgUrl, imgSrcset, title }) => {
  return (
    <div className="guideline">
      <img src={imgUrl} srcSet={imgSrcset} className="guideline__img" alt="" />
      <div className="guideline__content">
        <h2 className="guideline__text">{title}</h2>
        <img
          src={require("../images/arrow_right_icon.png")}
          srcSet={`${require("../images/arrow_right_icon@2x.png")} 2x, ${require("../images/arrow_right_icon@3x.png")}3x`}
          className="guideline__arrow_img"
          alt=""
        />
      </div>
    </div>
  );
};

class Section4 extends Component {
  static propTypes = {
    titles: PropTypes.arrayOf(PropTypes.string)
  };

  static defaultProps = {
    titles: ["development", "Design", "Business", "Arts"]
  };

  constructor(props) {
    super(props);
    this.state = {
      activeIndex: 0
    };
    this._handleItemClick = this._handleItemClick.bind(this);
  }

  _handleItemClick(event) {
    console.log("item被点击了---" + event.currentTarget.id);
    var index = event.currentTarget.id;
    this.setState({
      activeIndex: index
    });
  }

  render() {
    var { titles } = this.props;
    var { activeIndex } = this.state;
    return (
      <div className="section4-container">
        <SectionTitle
          title="OUR DESIGN GUIDELINES"
          describe="Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip."
        />
        <ul className="section4-container__list">
          {titles.map((item, index) => {
            return (
              <li
                className={`section4-container__item ${
                  index == activeIndex ? "active" : ""
                }`}
                key={index}
                id={index}
                onClick={this._handleItemClick}
              >
                <a className="section4-container__content">{item}</a>
              </li>
            );
          })}
        </ul>
        <div className="section4-container__guidelines">
          <Guideline
            id="1"
            imgUrl={require("../images/guideline-icon.png")}
            title="DESIGN LISTING  ONE"
            imgSrcset={`${require("../images/guideline-icon@2x.png")} 2x, ${require("../images/guideline-icon@3x.png")}3x`}
          />
          <Guideline
            id="2"
            imgUrl={require("../images/guideline-icon.png")}
            title="DESIGN LISTING  ONE"
            imgSrcset={`${require("../images/guideline-icon@2x.png")} 2x, ${require("../images/guideline-icon@3x.png")}3x`}
          />
          <Guideline
            id="3"
            imgUrl={require("../images/guideline-icon.png")}
            title="DESIGN LISTING  ONE"
            imgSrcset={`${require("../images/guideline-icon@2x.png")} 2x, ${require("../images/guideline-icon@3x.png")}3x`}
          />
        </div>
      </div>
    );
  }
}

export default Section4;

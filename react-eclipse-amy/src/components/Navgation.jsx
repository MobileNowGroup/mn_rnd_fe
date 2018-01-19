import React, { Component } from "react";
import "../styles/css/main.css";
import PropTypes from "prop-types";
import { NavLink } from "react-router-dom";

class Navgation extends Component {
  static propTypes = {
    navList: PropTypes.array,
    activeIndex: PropTypes.number
  };

  static defaultPropTypes = {
    activeIndex: -1
  };

  render() {
    let { activeIndex, navList } = this.props;
    return (
      <ul className="navigation__list">
        {navList.map((item, index) => {
          return index != navList.length - 1 ? (
            <li
              key={index}
              className={`navigation__item ${
                index == activeIndex ? "active" : ""
              }`}
            >
              <NavLink className="navigation__content" to={item.path}>
                {item.txt}
              </NavLink>
            </li>
          ) : (
            <NavLink key={index} className="navigation__item" to={item.path}>
              <a className="navigation__lastContent">{item.txt}</a>
              <img
                className="navigation__loginImage"
                src={require("../images/arrow_right.png")}
                alt=""
                srcSet={`${require("../images/arrow_right@2x.png")} 2x, ${require("../images/arrow_right@3x.png")}3x`}
              />
            </NavLink>
          );
        })}
      </ul>
    );
  }
}

export default Navgation;

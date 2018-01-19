import React, { Component } from "react";
import "../styles/css/main.css";
import PropTypes from "prop-types";
import { NavLink } from "react-router-dom";

class NavgationList extends Component {
  static propTypes = {
    navList: PropTypes.array.isRequired,
    activeIndex: PropTypes.number,
    handleItemClick: PropTypes.func.isRequired
  };

  static defalutPorpTypes = {
    activeIndex: -1
  };

  render() {
    let { activeIndex, navList, handleItemClick } = this.props;

    return (
      <div className="header__nav">
        <ul className="header__nav-list">
          {navList.map((item, index) => {
            return (
              <li
                key={index}
                className={`header__nav-item ${
                  index == activeIndex ? "active" : ""
                }`}
              >
                <NavLink
                  className="header__nav-content"
                  to={item.path}
                  onClick={handleItemClick}
                >
                  {item.txt}
                </NavLink>
              </li>
            );
          })}
        </ul>
      </div>
    );
  }
}

export default NavgationList;

import React, { Component } from "react";
import Navgation from "./Navgation";
import NavgationList from "./NavgationList";
import "../styles/css/main.css";
import { NavLink } from "react-router-dom";

export default class Header extends Component {
  constructor() {
    super();
    this.state = {
      checked: false,
      navList: [
        {
          path: "/about",
          txt: "About"
        },
        {
          path: "/studio",
          txt: "STUDIO"
        },
        {
          path: "/pricing",
          txt: "PRICING"
        },
        {
          path: "/blog",
          txt: "blog"
        },
        {
          path: "/join",
          txt: "Join"
        }
      ]
    };
  }

  //checkbox点击事件
  _handleCheckAction() {
    this.setState({
      checked: !this.state.checked
    });
  }

  //纵式导航栏点击事件
  _handleItemClick() {
    console.log("_handleItemClick");
    // let checkbox = document.getElementById("header__nav-checkbox");
    // console.log(checkbox);
    this.setState({
      checked: false
    });
  }

  render() {
    let { navList } = this.state;

    return (
      <div className="header">
        <NavLink to="/">
          <img
            className="header__home-button"
            src={require("../images/top_arrow.png")}
            alt=""
            srcSet={`${require("../images/top_arrow@2x.png")} 2x, ${require("../images/top_arrow@3x.png")}3x`}
          />
        </NavLink>
        <Navgation navList={navList} />
        <input
          type="checkbox"
          className="header__nav-checkbox"
          id="navi-toggler"
          checked={this.state.checked}
          onClick={this._handleCheckAction.bind(this)}
        />
        <label htmlFor="navi-toggler" className="header__nav-button">
          <span className="header__nav-button-icon"> &nbsp; </span>
        </label>
        <NavgationList
          navList={navList}
          handleItemClick={this._handleItemClick.bind(this)}
        />
      </div>
    );
  }
}

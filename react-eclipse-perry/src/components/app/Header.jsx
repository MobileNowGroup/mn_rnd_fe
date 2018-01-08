import React, {Component} from "react";
import {NavLink} from 'react-router-dom';
import "../../css/Header.css";
import loginArrow from "../../images/white-right-arrow.svg";

class Header extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div className="header">
      <NavLink exact="exact" className="logo" to="/">
        <span className="logo-text">
          Eclipse
        </span>
      </NavLink>
      <div className="right-of-header">
        <nav className="navigation">
          <ul>
            <li>
              <NavLink exact="exact" className="navigation-text" to='/about'>
                ABOUT
              </NavLink>
            </li>
            <li>
              <NavLink exact="exact" className="navigation-text" to='/studio'>
                STUDIO
              </NavLink>
            </li>
            <li>
              <NavLink exact="exact" className="navigation-text" to='/pricing'>
                PRICING
              </NavLink>
            </li>
            <li>
              <NavLink exact="exact" className="navigation-text" to='/blog'>
                BLOG
              </NavLink>
            </li>
          </ul>
        </nav>
        <div className="login-button">
          <NavLink className="login-navlink" exact="exact" to='/login'>
            <span className="login-text">Join</span>
            <img className="login-arrow" src={loginArrow}/>
          </NavLink>
        </div>
      </div>
    </div>);
  }
}

export default Header;

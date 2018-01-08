import React, {Component} from 'react';
import {NavLink} from 'react-router-dom';
import "../../css/MobileMenu.css";

class MobileMenu extends Component {
  constructor(props) {
    super(props);
    this.state = {
      show: false
    };
    this.onClick = this.onClick.bind(this);
  }

  // componentWillMount() {
  //   this.setState({show: this.props.show});
  // }

  componentWillReceiveProps(nextProps) {
    // this.setState({show: this.props.show});
  }

  onClick() {
    // this.setState({show: false})
    this.props.callback();
  }

  render() {
    return <div className="mobile-menu" id={this.props.show
        ? "mobile-menu-show"
        : "mobile-menu-hide"}>
      <ul className="mobile-navigation">
        <li>
          <NavLink onClick={this.onClick} exact="exact" className="mobile-navigation-text" to='/about'>
            ABOUT
          </NavLink>
        </li>
        <li>
          <NavLink onClick={this.onClick} exact="exact" className="mobile-navigation-text" to='/studio'>
            STUDIO
          </NavLink>
        </li>
        <li>
          <NavLink onClick={this.onClick} exact="exact" className="mobile-navigation-text" to='/pricing'>
            PRICING
          </NavLink>
        </li>
        <li>
          <NavLink onClick={this.onClick} exact="exact" className="mobile-navigation-text" to='/blog'>
            BLOG
          </NavLink>
        </li>
      </ul>
    </div>;
  }
}

export default MobileMenu;

import React, { Component } from "react";
import NavigationItem from "./NavigationItem";

class Navigation extends Component {
  constructor(props) {
    super(props);
    this.state = {};
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(id) {
    if (id === 0) {
      this.props.onClick(id);
    }
  }

  render() {
    const { activeId, isActive } = this.props;
    const icons = [
      [
        require("../../../img/icon-house.png"),
        require("../../../img/icon-house@2x.png"),
        require("../../../img/icon-house@3x.png")
      ],
      [
        require("../../../img/icon-diamond.png"),
        require("../../../img/icon-diamond@2x.png"),
        require("../../../img/icon-diamond@3x.png")
      ],
      [
        require("../../../img/icon-lookup.png"),
        require("../../../img/icon-lookup@2x.png"),
        require("../../../img/icon-lookup@3x.png")
      ]
    ];
    return (
      <nav className="navigation">
        <ul className="navigation__list">
          {icons.map((icons, i) => (
            <li className="navigation__item">
              <NavigationItem
                isActive={activeId === i ? isActive : false}
                id={i}
                onClick={this.handleClick}
                icons={icons}
              />
            </li>
          ))}
        </ul>
      </nav>
    );
  }
}

export default Navigation;

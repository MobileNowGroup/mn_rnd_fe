import React, { Component } from "react";

class NavigationItem extends Component {
  constructor(props) {
    super(props);
    this.state = {};
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    this.props.onClick(this.props.id);
  }

  render() {
    const { icons, isActive } = this.props;
    const className =
      "navigation-item" + (isActive ? " navigation-item--active" : "");
    return (
      <button className={className} onClick={this.handleClick}>
        <img
          className="navigation-item__icon scaled-img"
          src={icons[0]}
          srcset={`${icons[1]} 2x, ${icons[2]} 3x`}
          alt=""
        />
      </button>
    );
  }
}

export default NavigationItem;

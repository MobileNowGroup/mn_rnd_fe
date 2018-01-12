import React, { Component } from "react";
import { connect } from "react-redux";
import togglePanel from "../../actions/panel";

class NavigationItem extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  handleClick = () => {
    // this.props.onClick(this.props.id);
    this.props.togglePanel(!this.props.showPanel);
  };

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

const mapStateToProps = state => {
  return {
    showPanel: state.panel.showPanel
  };
};

export default connect(mapStateToProps, { togglePanel })(NavigationItem);

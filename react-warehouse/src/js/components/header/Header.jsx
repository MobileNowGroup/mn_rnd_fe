import React, { Component } from "react";
import logo from "../../../img/logo.svg";
import Designer from "../home/Designer";
import Navigation from "./Navigation";
import Panel from "./Panel";
import { connect } from "react-redux";
import togglePanel from "../../actions/panel";

class Header extends Component {
  state = {
    showPanel: false,
    activeId: -1
  };

  handleClick = id => {
    if (id === 0) {
      this.setState(prevState => ({
        showPanel: !prevState.showPanel,
        activeId: id
      }));
    }
  };

  render() {
    const { showPanel, activeId } = this.props;
    const panelClassName = `header__panel-container${
      showPanel
        ? " header__panel-container--show"
        : " header__panel-container--hidden"
    }`;
    return (
      <div className="header">
        <div className="header__logo-container">
          <img className="header__logo" src={logo} alt="logo" />
        </div>
        <div className="header__container">
          <div className="header__slider-container">
            <div className="header__slider">
              <div className="header__slider-img-container">
                <img
                  className="header__slider-img scaled-img"
                  src={require("../../../img/salt-bottle.png")}
                  srcSet={`${require("../../../img/salt-bottle@2x.png")} 2x, ${require("../../../img/salt-bottle@3x.png")} 3x`}
                  alt=""
                />
              </div>
            </div>
            <div className="header__heading">
              <div className="item-detail__heading">
                <h2 className="item-detail__name">Cork Salt & Pepper</h2>
                <h3 className="item-detail__price">45.50 $</h3>
              </div>
            </div>
            <div className="header__photo" />
          </div>
          <div className="header__designer">
            <Designer
              img={require("../../../img/figure-img-1.jpg")}
              caption="BY MATERIA & NENDO"
              heading="Good     Things come in Paris"
            />
          </div>
          <div className="header__navigation-container">
            <Navigation
              onClick={this.handleClick}
              activeId={activeId}
              isActive={showPanel}
            />
          </div>
        </div>
        <div className={panelClassName}>
          <Panel onClick={this.handleClick} />
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    showPanel: state.panel.showPanel,
    activeId: state.panel.activeId
  };
};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    togglePanel: () => {
      dispatch(togglePanel(!ownProps.showPanel));
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Header);

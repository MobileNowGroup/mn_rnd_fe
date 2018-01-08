import React, { Component } from "react";
import logo from "../../../img/logo.svg";
import Designer from "../home/Designer";
import Navigation from "./Navigation";
import Panel from "./Panel";

class Header extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showPanel: false,
      activeId: -1
    };
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(id) {

    if (id === 0) {
      this.setState(prevState => {
        return {
          showPanel: !prevState.showPanel,
          activeId: id
        };
      });
    }
  }

  render() {
    const { showPanel, activeId } = this.state;
    const panelClassName =
      "header__panel-container" +
      (showPanel
        ? " header__panel-container--show"
        : " header__panel-container--hidden");
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
                  srcset={`${require("../../../img/salt-bottle@2x.png")} 2x, ${require("../../../img/salt-bottle@3x.png")} 3x`}
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

export default Header;

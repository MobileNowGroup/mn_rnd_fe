import React from "react";

const PanelItem = ({ name, icon }) => (
  <div className="panel-item">
    <img className="panel-item__icon scaled-img" src={icon} alt="" />
    <span className="panel-item__name">{name}</span>
  </div>
);

export default PanelItem;

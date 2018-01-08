import React, {Component} from 'react';
import '../../css/Design.css';
import rightArrow from "../../images/green-right-arrow.svg";
import desginImg from "../../images/design-img.jpg";

class Design extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div className="design-wrap">
      <img className="design-img" src={desginImg} alt=""/>
      <div className="design-bottom-wrap">
        <h3 className="design-title">DESIGN LISTING ONE</h3>
        <img className="design-arrow" src={rightArrow} alt=""/>
      </div>
    </div>);
  }
}

export default Design;

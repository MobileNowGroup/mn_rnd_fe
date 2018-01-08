import React, {Component} from 'react';
import rightArrow from "../../images/blue-right-arrow.svg";
import downArrow from "../../images/blue-down-arrow.svg";

class Input extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    let buttonImg;
    if (this.props.name === "Email") {
      buttonImg = rightArrow;
    } else {
      buttonImg = downArrow;
    }
    return (
      <div className="input-container">
        <input type="text" name={this.props.name} placeholder={this.props.name} className="form-input"/>
        <button className="image-button">
          <img src={buttonImg}/>
        </button>
      </div>
    );
  }
}

export default Input;

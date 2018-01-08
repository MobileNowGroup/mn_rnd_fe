import React, {Component} from 'react';
import "../../css/Feature.css";

class Feature extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div className="feature-container">
      <img src={this.props.image} className="feature-image"/> {/* </div> */}
      <h3 className="feature-title">Lorem ipsum dolor sit</h3>
      <p className="feature-content">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim</p>
    </div>);
  }
}

export default Feature;

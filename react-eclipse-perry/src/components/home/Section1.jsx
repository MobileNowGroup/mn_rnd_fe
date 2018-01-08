import React, {Component} from "react";
import Input from "./Input";
import "../../css/Section1.css";

class Section1 extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div className="section1">
      <div className="section1-container">
        <h1 className="section1-title">Push yourself & your designs</h1>
        <p className="section1-content">
          Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
        </p>
        <form className="section1-form">
          <div className="form-group">
            <Input name="Design Style"/>
          </div>
          <div className="form-group">
            <Input name="Email"/>
          </div>
          <div className="form-group">
            <button type="submit" className="form-button"/>
          </div>
        </form>
      </div>
    </div>);
  }
}

export default Section1;

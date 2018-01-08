import React, {Component} from 'react';
import '../../css/Section4.css';
import Design from './Design';

class Section4 extends Component {
  constructor(props) {
    super(props);
    this.state = {
      buttonSelected: "0",
      buttons: []
    };
    this.buttonClicked = this.buttonClicked.bind(this);
  }

  componentWillMount() {
    const titles = ["DEVELOPMENT", "DESIGN", "BUSINESS", "ARTS"];
    for (let i = 0; i < titles.length; i++) {
      const title = titles[i];
      let className = "btn btn-secondary";
      if (i === 0) {
        className = `${className} btn-selected`;
      }
      this.state.buttons.push(<button type="button" className={className} onClick={this.buttonClicked} id={i}>{title}</button>);
    }
  }

  buttonClicked(e) {
    console.log(e.target.id, this.state.buttonSelected);
    if (this.state.buttonSelected !== e.target.id) {
      const titles = ["DEVELOPMENT", "DESIGN", "BUSINESS", "ARTS"];
      const buttons = [];
      for (let i = 0; i < titles.length; i++) {
        const title = titles[i];
        let className = "btn btn-secondary";
        if (i === parseInt(e.target.id)) {
          className = `${className} btn-selected`;
        }
        buttons.push(<button type="button" className={className} onClick={this.buttonClicked} id={i}>{title}</button>);
      }
      this.setState({buttonSelected: e.target.id, buttons});
    }
  }

  render() {
    return (<div className="section4-container">
      <h2 className="section4-title">OUR DESIGN GUIDELINES</h2>
      <p className="section4-content">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
      </p>
      <div className="btn-group" role="group" aria-label="btn group">
        {this.state.buttons}
      </div>
      <div className="design-containner">
        <Design/>
        <Design/>
        <Design/>
      </div>
    </div>);
  }
}

export default Section4;

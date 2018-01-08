import React, {Component} from 'react';
import '../../css/Section6.css';
import macbook from "../../images/macbook.png";
import macbook2x from "../../images/macbook@2x.png";
import macbook3x from "../../images/macbook@3x.png";
import upArrow from "../../images/white-up-arrow.svg";
import twitterIcon from "../../images/twitter-icon.svg";
import facebookIcon from "../../images/facebook-icon.svg";

class Section6 extends Component {
  constructor(props) {
    super(props);
    this.state = {};
    this.scrollToTop = this.scrollToTop.bind(this);
  }

  scrollToTop() {
    window.scroll(0, 0);
  }

  render() {
    return (<div className="section6-wrap">
      <div className="section6-left">
        <button className="btn section6-btn" onClick={this.scrollToTop}>
          <img src={upArrow} className="up-arrow" alt=""/>
        </button>
      </div>
      <div className="section6-right">
        <img src={macbook} alt="Macbook" srcSet={`${macbook2x} 2x, ${macbook3x} 3x`} className="section6-macbook"/>
        <div className="share-wrap">
          <a className="twitter-btn">
            <img src={twitterIcon} alt="twitter" className="twitter-icon"/>
            <span className="share-text">Twitter</span>
          </a>
          <a className="facebook-btn">
            <img src={facebookIcon} alt="facebook" className="facebook-icon"/>
            <span className="share-text">Facebook</span>
          </a>
        </div>
      </div>
    </div>);
  }
}

export default Section6;

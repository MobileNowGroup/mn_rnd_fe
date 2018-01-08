import React, {Component} from 'react';
import "../../css/Footer.css";

class Footer extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div className="footer-wrap">
      <div className="footer-left">
        <div className="footer-column1">
          <ul>
            <li>
              <a className="footer-link" href="#">About Us</a>
            </li>
            <li>
              <a className="footer-link" href="#">Support</a>
            </li>
            <li>
              <a className="footer-link" href="#">Why Buy</a>
            </li>
            <li>
              <a className="footer-link" href="#">Affiliates</a>
            </li>
          </ul>
        </div>
        <div className="footer-column2">
          <ul>
            <li>
              <a className="footer-link" href="#">Contact Us</a>
            </li>
            <li>
              <a className="footer-link" href="#">Returns</a>
            </li>
          </ul>
        </div>
      </div>
      <div className="footer-right">
        <span>Made in Melbourne By BlazRobar.com @BlazRobar</span>
        <span>Copyright © 2016, Cool stuff here</span>
      </div>
    </div>);
  }
}

export default Footer;

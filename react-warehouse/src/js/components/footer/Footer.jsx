import React, { Component } from 'react';

class Footer extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <div className="footer">
        <div className="footer__company-info">
          <div className="footer__icon-container">
            <img
              className="footer__icon"
              src={require('../../../img/footer-icon-tea-pots.png')}
              srcSet={`${require('../../../img/footer-icon-tea-pots@2x.png')} 2x, ${require('../../../img/footer-icon-tea-pots@3x.png')} 3x`}
              alt=""
            />
            <img
              className="footer__icon"
              src={require('../../../img/footer-icon-shake.png')}
              srcSet={`${require('../../../img/footer-icon-shake@2x.png')} 2x, ${require('../../../img/footer-icon-shake@3x.png')} 3x`}
              alt=""
            />
            <img
              className="footer__icon"
              src={require('../../../img/footer-icon-authentics.png')}
              srcSet={`${require('../../../img/footer-icon-authentics@2x.png')} 2x, ${require('../../../img/footer-icon-authentics@3x.png')} 3x`}
              alt=""
            />
          </div>
          <span className="footer__desp">
            2014 WARE HOUSE, USA based company. Image right belong to their respective owners.
          </span>
        </div>
        <div className="footer__company-logo">
          <div>
            <img
              className="footer__logo"
              src={require('../../../img/logo.svg')}
              alt=""
            />
            <img
              className="footer__child"
              src={require('../../../img/img-child.jpg')}
              srtset={`${require('../../../img/img-child@2x.jpg')} 2x, ${require('../../../img/img-child@3x.jpg')} 3x`}
              alt=""
            />
          </div>
          <span className="footer__title">warehouse</span>
        </div>
      </div>
    );
  }
}

export default Footer;

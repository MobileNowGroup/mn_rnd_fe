import React, { Component } from 'react'
import PropTypes from 'prop-types';
import '../styles/css/main.css'

const CompanyInfo = ({titles,handleClick}) => {

  CompanyInfo.propTypes = {
    titles:PropTypes.arrayOf(PropTypes.string).isRequired,
  }

  return (
    <ul className='companyInfo'>
      {titles.map((item) => {
        return (
          <li key={item} className='companyInfo__item'><a className='companyInfo__content'>{item}</a></li>
        )
      })}
    </ul>
  )
}

class Footer extends Component {

  handleClick(event) {
    console.log('列表被点击了');
  }

  render() {
    return(
      <div className='footer-container'>
        <div className='footer-container__about-us'>
          <div className='footer-container__about-list'>
            <div className='footer-container__company-info1'>
              <CompanyInfo
                titles={['About Us','Support','Why Buy','Affiliates']}
                handleClick={this.handleClick.bind(this)}
              />
            </div>
            <div className='footer-container__company-info2'>
              <CompanyInfo
                titles={['Contact Us','Returns']}
                handleClick={this.handleClick.bind(this)}
              />
            </div>
          </div>
          <p className='footer-container__contact-address'>Lorem ipsum dolor sit amet, consectetur adipisicing </p>
        </div>
        <div className='footer-container__contact-us'>
          <div className='footer-container__contact-net'>
            <img
              src={require('../images/tweet.png')}
              srcSet={`${require('../images/tweet@2x.png')} 2x, ${require('../images/tweet@3x.png')}3x`}
              alt=''
              className='footer-container__contact-net-img'
            />
            <img
              src={require('../images/fb.png')}
              srcSet={`${require('../images/fb@2x.png')} 2x, ${require('../images/fb@3x.png')}3x`}
              alt=''
              className='footer-container__contact-net-img'
            />
          </div>
          <p className='footer-container__contact-copyright'>Made in Melbourne By BlazRobar.com @BlazRobar<br/>Copyright © 2016, Cool stuff here</p>
        </div>
      </div>
    )
  }
}







export default Footer;

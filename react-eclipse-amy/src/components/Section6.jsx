import React, { Component } from 'react'
import '../styles/css/main.css'

class Section6 extends Component {

  //返回顶部
  _handleBackToTop() {
    console.log('返回顶部');
    document.body.scrollTop = document.documentElement.scrollTop = 0;
  }

  render () {
    return (
      <div className='section6-container'>
        <img
          className='section6-container__back-top'
          src={require('../images/back-to-top.png')}
          srcSet={`${require('../images/back-to-top@2x.png')} 2x, ${require('../images/back-to-top@3x.png')}3x`}
          alt=''
          onClick={this._handleBackToTop.bind(this)}
        />
      </div>
    );
  }
}

export default Section6;

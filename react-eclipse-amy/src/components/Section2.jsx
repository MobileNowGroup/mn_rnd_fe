
import React, { Component } from 'react'
import '../styles/css/main.css'

class Section2 extends Component {
  render() {
    return (

      <div className='section2'>
        <div className='content'>
          <label className='section2__title1'>LATEST DESIGN STYE</label>
          <label className='section2__title2'>
            Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
          </label>
        </div>
        <img
          className='section2__phone-image'
          src={require('../images/i-phone-6.png')}
          alt=''
          srcSet={`${require('../images/i-phone-6@2x.png')} 2x, ${require('../images/i-phone-6@3x.png')}3x`}
        />
      </div>
    );
  }
}

export default Section2;

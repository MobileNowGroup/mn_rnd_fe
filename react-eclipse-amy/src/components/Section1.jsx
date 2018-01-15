
import React,{ Component } from 'react'
import '../styles/css/main.css'
import Form from '../const/Form';


class Section1 extends Component {
  render() {
    return (
      <div className='section1'>
        <div className='section1__content'>
          <label className='section1__title1'>Push yourself <br/> & your <br/>designs</label>
          <label className='section1__title2'>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. </label>
        </div>
        <Form />
      </div>
    );
  }
}

export default Section1;

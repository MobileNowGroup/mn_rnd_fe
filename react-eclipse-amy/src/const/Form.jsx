
import React, { Component } from 'react'
import '../styles/css/main.css'

export const MyInput = ({handleChange,placeholderStr,imgUrl,imgSrcset}) => {
  return (
    <div className='myInput'>
      <input
        className='myInput__input'
        type='text'
        placeholder={placeholderStr}
        onChange={handleChange}
      />
      <img
        className='myInput__image'
        src={imgUrl}
        alt=''
        srcSet={imgSrcset}
      />
    </div>
  );
}

class Form extends Component {
  state = {
      title: '内容',
  }

  updateChange(event) {
   this.setState({ title: event.target.value })
  }

  updateEmailChange(event) {

  }

  render() {
    return (
      <div className='form'>
        <MyInput
          id='Design'
          handleChange={this.updateChange.bind(this)}
          placeholderStr='Design Style'
          imgUrl={require('../images/input_arrow_down.png')}
          imgSrcset={`${require('../images/input_arrow_down@2x.png')} 2x, ${require('../images/input_arrow_down@3x.png')}3x`}
        />
        <MyInput
          id='Email'
          handleChange={this.updateEmailChange.bind(this)}
          placeholderStr='Email'
          imgUrl={require('../images/input_arrow_right.png')}
          imgSrcset={`${require('../images/input_arrow_right@2x.png')} 2x, ${require('../images/input_arrow_right@3x.png')}3x`}
        />
        <button className='form__login_button'>
          <img
            className='form__login_button_img'
            alt=''
            src={require('../images/arrow_right.png')}
            srcSet={`${require('../images/arrow_right@2x.png')} 2x, ${require('../images/arrow_right@3x.png')}3x`}
          />
        </button>

      </div>
    )
  }
}

export default Form;

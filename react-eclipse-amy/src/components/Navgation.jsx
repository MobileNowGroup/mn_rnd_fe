
import React,{ Component } from 'react'
import '../styles/css/main.css'

class Navgation extends Component {
  render() {
    return (
      <ul className='navigation__list'>
        <li className='navigation__item'><a className='navigation__content' href='#about'>About</a></li>
        <li className='navigation__item'><a className='navigation__content' href='#STUDIO'>STUDIO</a></li>
        <li className='navigation__item'><a className='navigation__content' href='#PRICING'>PRICING</a></li>
        <li className='navigation__item'><a className='navigation__content' href='#blog'>blog</a></li>
        <li className='navigation__item'>
          <a className='navigation__lastContent' href='#Join'>Join</a>
          <img
            className='navigation__loginImage'
            src={require('../images/arrow_right.png')}
            alt=''
            srcSet={`${require('../images/arrow_right@2x.png')} 2x, ${require('../images/arrow_right@3x.png')}3x`}
          />
        </li>
      </ul>
    );
  }
}

export default Navgation;

import React,{ Component } from 'react';
import Navgation from './Navgation';
import '../styles/css/main.css'

 export default class Header extends Component {

   constructor() {
     super();
     this.state = {
       checked: false,
     }
   }

   handleCheckAction() {
     this.setState({
       checked: !this.state.checked,
     })
   }

   render() {
     return (
       <div className='header'>
         <img
           className='header__home-button'
           src={require('../images/top_arrow.png')}
           alt=''
           srcSet={`${require('../images/top_arrow@2x.png')} 2x, ${require('../images/top_arrow@3x.png')}3x`}
         />
         <Navgation />
         <input
           type="checkbox"
           className='header__nav-checkbox'
           id='navi-toggler'
           checked={this.state.checked}
           onClick={this.handleCheckAction.bind(this)}
         />
         <label htmlFor='navi-toggler' className='header__nav-button' >
           <span className='header__navbar-toggler-icon'></span>
         </label>
         <div className='header__nav'>
           <ul className='header__nav-list'>
             <li className='header__nav-item'><a className='header__nav-content' href=''>Home</a></li>
            <li className='header__nav-item'><a className='header__nav-content' href=''>About</a></li>
            <li className='header__nav-item'><a className='header__nav-content' href=''>STUDIO</a></li>
            <li className='header__nav-item'><a className='header__nav-content' href=''>PRICING</a></li>
            <li className='header__nav-item'><a className='header__nav-content' href=''>blog</a></li>
          </ul>
         </div>
        </div>
     );
   }
 }

import React from 'react';
import Figure from './Figure';

const Designer = props => {
  const {img, caption, heading} = props;
  return <div className='designer'>
    <Figure img={img} caption={caption}/>
    <span className='designer__heading'>{heading}</span>
  </div>;
};

export default Designer;

import React from 'react';

const Figure = props => {
  const {img, caption} = props;
  return <figure className='figure'>
    <img className='figure__img' src={img} alt=''/>
    <figcaption className='figure__caption'>{caption}</figcaption>
  </figure>;;
};

export default Figure;

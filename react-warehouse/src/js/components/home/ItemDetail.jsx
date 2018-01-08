import React from 'react';
import Designer from './Designer';

const ItemDetail = props => {
  const {
    img,
    caption,
    heading,
    name,
    price,
    photos
  } = props;
  return <div className='item-detail'>
    <div className='item-detail__heading'>
      <h2 className='item-detail__name'>{name}</h2>
      <h3 className='item-detail__price'>{price}</h3>
      <button className='item-detail__fav-button'>
        <svg class="item-detail__fav-icon" width={8} height={9} style={{
            // fill: '#fa857b'
          }}>
          <use xlinkHref={`${require('../../../img/heart.svg')}#heart`}></use>
        </svg>
        <span className='item-detail__fav-text'>ADD TO FAVS</span>
      </button>
    </div>
    <img className='item-detail__photo' src={photos[0]} srcset={`${photos[1]} 2x, ${photos[2]} 3x`} alt=''/>
    <Designer img={img} caption={caption} heading={heading}/>
  </div>;
};

export default ItemDetail;

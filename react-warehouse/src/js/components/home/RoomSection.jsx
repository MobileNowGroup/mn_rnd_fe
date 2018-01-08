import React from 'react';

const RoomSection = props => {
  return <div className='room-section'>
    <div className='room-section__room'>
      <img className='room-section__icon' src={require('../../../img/icon-room.svg')} alt=''/>
      <span className='room-section__title'>Rooms</span>
    </div>
  </div>;
};

export default RoomSection;

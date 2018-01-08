import React, {Component} from 'react';
import ItemCategory from './ItemCategory';
import ItemDetail from './ItemDetail';

class ItemSection extends Component {
  constructor(props) {
    super(props);
    this.state = {};
    this.swap = this.swap.bind(this);
  }

  swap(array) {
    const temp = array[0];
    array[0] = array[1];
    array[1] = temp;
    return array;
  }

  render() {
    const {
      categoryIcons,
      categoryName,
      bg,
      isCategoryLeft,
      img,
      caption,
      heading,
      name,
      price,
      photos
    } = this.props;
    let components = [
      <ItemCategory categoryIcons={categoryIcons} categoryName={categoryName} bg={bg}/>,
      <ItemDetail photos={photos} name={name} price={price} img={img} caption={caption} heading={heading}/>
    ];
    return <div className='item-section'>
      {
        isCategoryLeft
          ? components
          : this.swap(components)
      }
    </div>;
  }
}

export default ItemSection;

import React, {Component} from 'react';
import closeIcon from '../../../img/blue-x.svg';
import PanelItem from './PanelItem';

class Panel extends Component {
  constructor(props) {
    super(props);
    this.state = {
      itemNames: [],
      itemIcons: []
    };
  }

  componentDidMount() {
    const itemNames = [
      'BOWLS  &  PANS',
      'CUPS',
      'DISPENSERS',
      'FURNITURE',
      'TABLEWARE',
      'CUTLERY',
      'PLANTS & DECO'
    ];
    const itemIcons = [];
    for (var i = 0; i < itemNames.length; i++) {
      itemIcons.push(require(`../../../img/panel-item-icon-${i}.png`));
    }
    this.setState({itemNames: itemNames, itemIcons: itemIcons});
  }

  handleClick = () => {
    this.props.onClick(0);
  }

  render() {
    const {itemNames, itemIcons} = this.state;
    return <div className='panel'>
      <div className='panel__header'>
        <button className='panel__close-button' onClick={this.handleClick}>
          <img className='panel__close-icon' src={closeIcon} alt='close panel'/>
        </button>
      </div>
      <div className='panel__items-container'>
        {itemNames.map((itemName, i) => <PanelItem name={itemNames[i]} icon={itemIcons[i]}/>)}
      </div>
    </div>;
  }
}

export default Panel;

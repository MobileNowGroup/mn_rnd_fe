import React, {Component} from 'react';
import ItemSection from '../components/home/ItemSection';
import RoomSection from '../components/home/RoomSection';

class Home extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return <div>
      <ItemSection photos={[require('../../img/img-tea-pot.png'), require('../../img/img-tea-pot@2x.png'), require('../../img/img-tea-pot@3x.png')]} name='Kettle Thermo Pot' price='274.50 $' img={require('../../img/figure-img-2.jpg')} caption='BY NOOR JAHAAN' heading='Tea      O’Clock' isCategoryLeft={true} bg={require('../../img/item-category-bg-blue.svg')} categoryIcons={[require('../../img/icon-tea-pot.png'), require('../../img/icon-tea-pot@2x.png'), require('../../img/icon-tea-pot@3x.png')]} categoryName='Seven pots'/>
      <RoomSection/>
      <ItemSection photos={[require('../../img/img-shake.png'), require('../../img/img-shake@2x.png'), require('../../img/img-shake@3x.png')]} name='Salt & Pepper  Grinder' price='75.50 $' img={require('../../img/figure-img-3.jpg')} caption='BY AMISHA PATEL' heading='Ebony & Ivory' isCategoryLeft={false} bg={require('../../img/item-category-bg-green.svg')} categoryIcons={[require('../../img/icon-shake.png'), require('../../img/icon-shake@2x.png'), require('../../img/icon-shake@3x.png')]} categoryName='Shakers'/>
      <ItemSection photos={[require('../../img/img-bowl.png'), require('../../img/img-bowl@2x.png'), require('../../img/img-bowl@3x.png')]} name='Generic Oak Bowl' price='87.49 $' img={require('../../img/figure-img-4.jpg')} caption='BY KARINA KAPOOR' heading='Back             to Basics' isCategoryLeft={true} bg={require('../../img/item-category-bg-orange.svg')} categoryIcons={[require('../../img/icon-authentics.png'), require('../../img/icon-authentics@2x.png'), require('../../img/icon-authentics@3x.png')]} categoryName='AUTHENTICS'/>
    </div>;
  }
}

export default Home;

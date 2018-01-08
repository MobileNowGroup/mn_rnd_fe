import React, {Component} from 'react';
import Section1 from "../components/home/Section1";
import Section2 from "../components/home/Section2";
import Section3 from "../components/home/Section3";
import Section4 from "../components/home/Section4";
import Section5 from "../components/home/Section5";
import Section6 from "../components/home/Section6";
import ContactUs from "../components/home/ContactUs";
import LazyLoad from 'react-lazyload';

class Home extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (<div>
      <Section1 key="section1"/>
      <LazyLoad height={592} once="once">
        <Section2 key="section2"/>
      </LazyLoad>
      <LazyLoad height={800} once="once">
        <Section3 key="section3"/>
      </LazyLoad>
      <LazyLoad height={700} once="once">
        <Section4 key="section4"/>
      </LazyLoad>
      <LazyLoad height={890} once="once">
        <Section5 key="section5"/>
      </LazyLoad>
      <LazyLoad height={400} once="once">
        <Section6 key="section6"/>
      </LazyLoad>
      <ContactUs/>
    </div>);
  }
}

export default Home;

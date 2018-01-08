import React, {Component} from 'react';
import logo from './logo.svg';
import './css/main.css';
import Header from './js/components/header/Header';
import Home from './js/pages/Home';
import Footer from './js/components/footer/Footer';

class App extends Component {
  render() {
    return (<div className="App">
      <Header/>
      <Home/>
      <Footer/>
    </div>);
  }
}

export default App;

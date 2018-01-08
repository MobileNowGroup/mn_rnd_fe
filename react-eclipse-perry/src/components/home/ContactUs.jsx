import React, {Component} from 'react';
import "../../css/ContactUs.css";

class ContactUs extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <div className="contact-us-container">
        <div className="contact-us-text">Contact Us</div>
      </div>
    );
  }
}

export default ContactUs;

import React from 'react';
import {Link} from 'react-router';

// <img src="img/logo-white.png"/> for npm

const Logo = React.createClass({

    render: function () {
        return (
            <Link to="/">
                <div className="logo">
                    <img src="public/img/logo.png"/>
                    <div> </div>
                </div>
            </Link>
        );
    }
});

export default Logo;
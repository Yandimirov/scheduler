import React from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import {Link} from 'react-router';
import {browserHistory} from 'react-router';
import HomeSearch from './HomeSearch'

const Home = React.createClass({

    render: function () {
        return <HomeSearch childrenProp={this.props.children} />
    }
});

export default Home;

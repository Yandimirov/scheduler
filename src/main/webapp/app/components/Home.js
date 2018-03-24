import React from 'react';
import HomeSearch from './HomeSearch'

const Home = React.createClass({

    render: function () {
        return <HomeSearch childrenProp={this.props.children} />
    }
});

export default Home;

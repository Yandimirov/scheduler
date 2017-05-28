import React from 'react';
import {Link} from 'react-router';
import {browserHistory} from 'react-router';
import UserLink from './UserLink.js'

const HomeSearch = React.createClass({
    render: function () {
        return (
            <div className="search">
                <div className="search-results">
                    {this.props.childrenProp}
                </div>
            </div>
        );
    }
});

export default HomeSearch;

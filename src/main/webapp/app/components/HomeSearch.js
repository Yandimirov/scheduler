import React from 'react';

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

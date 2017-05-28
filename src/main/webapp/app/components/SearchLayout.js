import React from 'react';

const SearchLayout = React.createClass({
    render: function () {
        return (
            <div className="search">
                <div className="search-results">
                    {this.props.children}
                </div>
            </div>
        );
    }
});

export default SearchLayout;

import React from 'react';
import {Link} from 'react-router';

import FontIcon from 'material-ui/FontIcon';
import IconButton from 'material-ui/IconButton';
import Chip from 'material-ui/Chip';
import {IMAGE_PATH} from "../paths.js";

const styles = {
    chip: {
        margin: 4,
    },
    wrapper: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    tooltip: {
        fontSize: 14
    }
};

const UserLink = React.createClass({

    changeSize(e){
        if (e.target.width > e.target.height) {
            e.target.width = 50 * (e.target.width / e.target.height);
            e.target.height = 50;
        }
        else {
            e.target.height = 50 * (e.target.height / e.target.width);
            e.target.width = 50;
        }

    },
    render: function () {
        const link = "/users/" + this.props.id;
        return (
            <div className="user-link">
                <div className="user-link-image-div"><img className="user-link-image"
                                                          src={IMAGE_PATH + this.props.imagePath}
                                                          onLoad={this.changeSize}/></div>
                <div className="user-name">
                    <Link to={link}> {this.props.firstName} {this.props.lastName}</Link>
                </div>
            </div>
        );
    }
});

export default UserLink;
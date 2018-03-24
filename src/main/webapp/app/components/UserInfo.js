import React from 'react';
import {Link} from 'react-router';
import axios from 'axios';
import {PATH_LOGOUT} from '../paths.js';
import UserPersonalInfo from './UserPersonalInfo.js';
import IconButton from 'material-ui/IconButton';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import MoreVertIcon from 'material-ui/svg-icons/navigation/more-vert';


const UserInfo = React.createClass({

    handleTouchTap: function (e) {
        axios.post(
            PATH_LOGOUT,
            {
                id: localStorage.getItem('userId'),
                token: localStorage.getItem('token')
            });
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
    },

    render: function () {

        const menu = <div className="icon-menu">
            <IconMenu
                iconButtonElement={
                    <IconButton><MoreVertIcon /></IconButton>
                }
                targetOrigin={{horizontal: 'left', vertical: 'top'}}
                anchorOrigin={{horizontal: 'left', vertical: 'top'}}
            >
                <MenuItem primaryText="Редактировать" containerElement={<Link to='/edit'/>}/>
                <MenuItem primaryText="Выйти из системы" onTouchTap={this.handleTouchTap}
                          containerElement={<Link to='/login'/>}/>
            </IconMenu>
        </div>;

        return (
            <div className="user-info">
                <UserPersonalInfo
                    firstName={this.props.firstName}
                    lastName={this.props.lastName}
                    birthday={this.props.birthday}
                    city={this.props.city}
                    email={this.props.email}
                    imagePath={this.props.imagePath}
                />
                {menu}
            </div>
        );
    }
});

export default UserInfo;
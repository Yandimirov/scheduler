import React from 'react';
import Avatar from 'material-ui/Avatar';
import FontIcon from 'material-ui/FontIcon';
import List from 'material-ui/List/List';
import ListItem from 'material-ui/List/ListItem';
import {IMAGE_PATH} from "../paths.js";


const UserPersonalInfo = React.createClass({
    changeSize(e){
        if (e.target.width > e.target.height) {
            e.target.width = 300 * (e.target.width / e.target.height);
            e.target.height = 300;
        }
        else {
            e.target.height = 300 * (e.target.height / e.target.width);
            e.target.width = 300;
        }
    },

    render(){
        const {
            firstName,
            lastName,
            birthday,
            city,
            email,
            imagePath
        } = this.props;
        return (
            <div className="user-personal-info">
                <div className="user-personal-image-div">
                    <img className="user-personal-image" src={IMAGE_PATH + imagePath} onLoad={this.changeSize}/>
                </div>
                <div className="inline">
                    <b>{firstName} {lastName} </b><br/>
                    <FontIcon className="material-icons">cake</FontIcon> {birthday} <br/>
                    <FontIcon className="material-icons">location_city</FontIcon> {city} <br/>
                    <FontIcon className="material-icons">email</FontIcon> {email}
                </div>
            </div>
        );
    }
});

export default UserPersonalInfo;
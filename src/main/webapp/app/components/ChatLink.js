import FontIcon from 'material-ui/FontIcon';
import React from 'react';
import {Link} from 'react-router';
import {IMAGE_PATH} from "../paths.js";

const ChatLink = React.createClass({
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
    render(){
        const link = "/chats/" + this.props.id;
        let chatName = '';
        if(typeof(this.props.name) === 'undefined'){
            chatName = this.props.firstName + " " + this.props.lastName;
        } else {
            chatName = this.props.name;
        }
        return (
            <div className="chat-link">
                <div className="user-link-image-div"><img className="user-link-image"
                                                          src={IMAGE_PATH + this.props.imagePath}
                                                          onLoad={this.changeSize}/></div>
                <div className="chat-name">
                    {chatName}
                </div>
            </div>
        )
    }
});

export default ChatLink;
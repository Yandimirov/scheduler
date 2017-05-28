import React from 'react';
import axios from 'axios';
import {getConfig, getPathApiUserMessages} from '../utils.js';
import {PATH_API_MESSAGE} from '../paths.js';
import RaisedButton from 'material-ui/RaisedButton';
import TextField from 'material-ui/TextField';
import FontIcon from 'material-ui/FontIcon';

const ChatMessageAdd = React.createClass({
    getInitialState(){
        return {
            textMessage: ''
        }
    },

    handleMessageSend(){
        this.props.messageSend("OK");
    },

    handleMesafeChange(event){
        this.setState(
            {
                textMessage: event.target.value
            }
        );
    },

    handleTouchTap(){
        axios.post(
            PATH_API_MESSAGE,
            {
                text: this.state.textMessage,
                chatId: this.props.chatId,
                timeStamp: new Date()
            },
            getConfig()).then(response => {
                this.props.messageSend("OK");
            }
        );
        this.state.textMessage = '';
    },
    render(){
        return (
            <div className="chat-message-add">
                <TextField
                    ref="messageText"
                    floatingLabelText="Сообщение"
                    multiLine={true}
                    rows={1}
                    rowsMax={1}
                    style={{width: "680px", verticalAlign: "top"}}
                    value={this.state.textMessage}
                    onChange={this.handleMesafeChange}
                />
                <RaisedButton className="add-btn"
                              backgroundColor="000000"
                              secondary={true}
                              onTouchTap={this.handleTouchTap}
                              icon={<FontIcon className="material-icons">send</FontIcon>}
                              style={{width: 100, height: 50, marginLeft: 20}}
                />
            </div>

        );
    }
});

export default ChatMessageAdd;

import React from 'react';
import axios from 'axios';
import RaisedButton from 'material-ui/RaisedButton';
import {getConfig, getPathApiUserMessages} from '../utils.js';
import TextField from 'material-ui/TextField';

const UserProfileMessage = React.createClass({

    handleMessageSend(){
        this.props.messageSend("OK");
    },

    handleTouchTap(){

        axios.post(
            getPathApiUserMessages(),
            {
                text: this.refs.messageText.getValue(),
                recipientId: this.props.userId,
                timeStamp: new Date()
            },

            getConfig()).then(response => {
                this.props.messageSend("OK");
            }
        );
    },
    render: function () {

        return (
            <div>
                <div className="label">Получатель: <span>{this.props.firstName} {this.props.lastName}</span></div>
                <TextField
                    ref="messageText"
                    floatingLabelText="Сообщение"
                    multiLine={true}
                    rows={3}
                    rowsMax={4}
                    style={{"width": "300px"}}
                /><br />
                <RaisedButton
                    backgroundColor="000000"
                    label="Отправить сообщение"
                    secondary={true}
                    onTouchTap={this.handleTouchTap}
                />
            </div>

        );
    }
});

export default UserProfileMessage;
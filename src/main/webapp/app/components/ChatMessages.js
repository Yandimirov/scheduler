import React from 'react';
import axios from 'axios';
import {getConfig, getPathApiUserChats, getPathApiUserChatsAfter} from '../utils.js';
import {Link} from 'react-router';
import ChatMessage from './ChatMessage.js';
import ChatMessageAdd from './ChatMessageAdd.js';
import RaisedButton from 'material-ui/RaisedButton';
import {IMAGE_PATH} from '../paths';

const ChatMessages = React.createClass({
    getInitialState(){
        return {
            intervalId: 0,
            messages: []
        }
    },

    handleTimeout(scroll){
        let messages = this.state.messages;
        if(messages.length == 0){
            axios.get(getPathApiUserChats() + this.props.params.chatId, getConfig()).then(response => {
                this.setState({
                    messages: response.data
                });
                this.scroll();
            });
        }else{
            axios.get(getPathApiUserChatsAfter() + this.props.params.chatId + '/' + messages[messages.length - 1].timeStamp,
                getConfig()
            ).then(response => {
                let newMessages = response.data;
                for(let i = 0; i < newMessages.length; i++){
                    messages.push(newMessages[i]);
                }
                this.setState({
                    messages: messages
                });
                if(scroll){
                    this.scroll()
                }
            });
        }
    },

    componentDidMount(){
        axios.get(getPathApiUserChats() + this.props.params.chatId, getConfig()).then(response => {
            this.setState({
                messages: response.data
            });
            this.scroll();
        });
        this.state.intervalId = setInterval(this.handleTimeout, 1000);
        this.scroll();
    },

    componentWillUnmount(){
        clearInterval(this.state.intervalId);
    },

    handleMessageSend(){
        this.handleTimeout(true);
    },

    scroll(){
        let block = document.getElementById("chat-message");
        block.scrollTop = block.scrollHeight + 1000;

    },
    //componentDidUpdate(){
    //  let block = document.getElementById("chat-message");
    //  block.scrollTop = block.scrollHeight;
    //},

    render(){
        const messages = this.state.messages.map(messages =>
            <ChatMessage senderId={messages.sender.id} senderName={messages.sender.firstName + " " + messages.sender.lastName} time={messages.timeStamp}
                         text={messages.text} key={messages.id}/>
        );
        return (
            <div>
                <div>
                    <Link to="/chats">
                        <RaisedButton label="Назад" primary={true} style={{marginRight: 30}}/>
                    </Link> Диалог
                </div>
                <ul className="chat-message" id="chat-message">
                    {messages}
                </ul>
                <ChatMessageAdd chatId={this.props.params.chatId} messageSend={this.handleMessageSend}/>
            </div>

        );
    }
});

export default ChatMessages;
import React from 'react';
import FontIcon from 'material-ui/FontIcon';
import ChatLink from './ChatLink.js'
import axios from 'axios';
import {getConfig, getPathApiUserChats} from '../utils.js';
import {Link} from 'react-router';
import CreateChat from './CreateChat.js';

const ChatList = React.createClass({
    getInitialState(){
        return {
            chats: []
        }
    },

    componentDidMount(){
        axios.get(getPathApiUserChats(), getConfig()).then(response => {
            this.setState({
                chats: response.data
            });
        });
    },

    render(){
        var link = "/chats/";
        const chats = this.state.chats.map(chat => {
            let filteredChat = '';
            if(chat.chat.name == null){
                filteredChat = <li key={chat.chat.id}>
                    <Link to={link + chat.chat.id}><ChatLink userId={chat.user.id} firstName={chat.user.firstName}
                                                             lastName={chat.user.lastName} key={chat.chat.id}
                                                             id={chat.chat.id} imagePath={chat.user.imagePath}/></Link>
                </li>
            }else {
                filteredChat = <li key={chat.chat.id}>
                    <Link to={link + chat.chat.id}><ChatLink name={chat.chat.name} key={chat.chat.id} id={chat.chat.id} imagePath={chat.chat.picture}/></Link>
                </li>
            }
            return filteredChat;
        });
        return (
            <div>
                <div>Все диалоги</div>
                <CreateChat style={{display: 'inline'}}/>
                <div className="chats">
                    <ul className="chat-list">
                        {chats}
                    </ul>
                </div>
            </div>
        );
    }
});

export default ChatList;
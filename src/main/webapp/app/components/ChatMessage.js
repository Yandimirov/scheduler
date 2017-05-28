import React from 'react';
import {Link} from 'react-router';

const ChatMessage = React.createClass({

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
        let date = new Date(this.props.time);
        var options = {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            timezone: 'UTC',
            hour: 'numeric',
            minute: 'numeric'
        };

        return (
            <li className={this.props.senderId == localStorage.getItem('userId') ? 'right-message' : 'left-message'}>
                <div>

                    <div className="message-info"> <Link to={"/users/" + this.props.senderId}>{this.props.senderName}</Link> {date.toLocaleString("ru", options)}</div>
                    <div>
                        {this.props.text}
                    </div>
                </div>
            </li>
        );
    }
});

export default ChatMessage;
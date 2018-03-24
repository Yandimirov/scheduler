import React from 'react';
import axios from 'axios';
import {PATH_API_USER} from '../paths.js';
import UserPersonalInfo from './UserPersonalInfo.js'
import {browserHistory} from 'react-router';
import {getConfig} from '../utils.js';
import {Tab, Tabs} from 'material-ui/Tabs';
import Scheduler from './Scheduler.js';
import MapContainer from './MapContainer.js';


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

const UserProfile = React.createClass({
    getInitialState(){
        return {
            user: {
                firstName: "",
                lastName: "",
                birthday: "",
                city: "",
                email: "",
                imagePath: ""
            },
            messageFormDisplay: "none"
        }
    },

    componentDidMount(){
        if (localStorage.getItem('userId') == this.props.params.userId) {
            browserHistory.push('/')
        } else {
            axios.get(PATH_API_USER + this.props.params.userId, getConfig()).then(response => {
                this.setState({
                    user: response.data
                });
            });
        }
        let config = {
            headers: {"x-auth-token": localStorage.getItem('token')}
        };
        axios.get(PATH_API_USER + this.props.params.userId, config).then(response => {
            this.setState({
                user: response.data
            });
        });
    },


    handleTouchTap(){
        this.setState({messageFormDisplay: (this.state.messageFormDisplay == 'none') ? 'block' : 'none'});
    },
    handleMessageSend(){

        this.handleTouchTap();
    },


    render: function () {
        return (

        <Tabs
            value={this.state.value}
            onChange={this.handleChange}
        >
            <Tab label="Профиль пользователя" value="a">
                <div className="profile">
                    <UserPersonalInfo
                        firstName={this.state.user.firstName}
                        lastName={this.state.user.lastName}
                        birthday={this.state.user.birthday}
                        city={this.state.user.city}
                        email={this.state.user.email}
                        imagePath={this.state.user.imagePath}
                    />
                </div>
            </Tab>
            <Tab label="Календарь" value="a">
                <div><Scheduler ref="scheduler" user={this.props.params.userId}/></div>
            </Tab>
            <Tab label="Карта" value="b">
                <MapContainer width={1470} height={900} event={null} user={this.props.params.userId}/>
            </Tab>
        </Tabs>
        );
    }
});

export default UserProfile;
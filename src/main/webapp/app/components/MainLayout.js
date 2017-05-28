import React from 'react';
import {Link} from 'react-router';
import Paper from 'material-ui/Paper';

import LoginForm from './LoginForm';
import Logo from './Logo';
import UserPersonalInfo from './UserPersonalInfo.js';
import UserInfo from './UserInfo';


import {cyan500} from 'material-ui/styles/colors';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import getMuiTheme from 'material-ui/styles/getMuiTheme';

import {BottomNavigation, BottomNavigationItem} from 'material-ui/BottomNavigation';
import FontIcon from 'material-ui/FontIcon';
const GroupIcon = <FontIcon className="material-icons">group</FontIcon>;
const EmailIcon = <FontIcon className="material-icons">email</FontIcon>;
import {browserHistory} from 'react-router';
import {PATH_API_USER} from '../paths.js';
import axios from 'axios';
import {getImagePath} from '../utils.js'


const muiTheme = getMuiTheme({});

const MainLayout = React.createClass({
    getInitialState() {
        return {
            userInfo: {
                firstName: "",
                lastName: "",
                birthday: "",
                city: "",
                email: "",
                imagePath: "",
            },
        }
    },

    handleShouldUpdate(e){
        let config = {
            headers: {"x-auth-token": localStorage.getItem('token')}
        };
        axios.get(PATH_API_USER + localStorage.getItem('userId'), config).then(response => {
            this.setState({
                userInfo: response.data
            });
        });
    },

    componentDidMount(){
        if (localStorage.getItem('token') == null || localStorage.getItem('userId') == null) {
            browserHistory.push('login');
        } else {
            let config = {
                headers: {"x-auth-token": localStorage.getItem('token')}
            };
            axios.get(PATH_API_USER + localStorage.getItem('userId'), config).then(response => {
                this.setState({
                    userInfo: response.data
                });
            });
        }
    },
    renderChild(){
        var child = null;
        if (this.props.children.type.displayName == "UserEditor") {
            child = React.cloneElement(
                this.props.children, {
                    firstName: this.state.userInfo.firstName,
                    lastName: this.state.userInfo.lastName,
                    birthday: this.state.userInfo.birthday,
                    city: this.state.userInfo.city,
                    email: this.state.userInfo.email,
                    imagePath: this.state.userInfo.imagePath,
                    shouldUpdate: this.handleShouldUpdate
                })
        }
        else if (this.props.children.type.displayName == "Home") {
            child = React.cloneElement(
                this.props.children, {
                    // TODO insert something
                })
        } else {
            child = this.props.children;
        }
        return child;
    },
    render: function () {
        return (
            <MuiThemeProvider muiTheme={muiTheme}>
                <Paper className="app" zDepth={0}>
                    <Paper zDepth={0} className="center-panel">
                        {this.renderChild()}
                    </Paper>
                    <div style={{
                        width: 400,
                        height: '100%',
                        position: 'fixed',
                        backgroundColor: '#00ACC1',
                        bottom: 0,
                        left: 0,
                        zIndex: 0
                    }}></div>
                    <Paper className="left-panel" zDepth={0}>
                        <Logo />
                        <UserInfo
                            firstName={this.state.userInfo.firstName}
                            lastName={this.state.userInfo.lastName}
                            birthday={this.state.userInfo.birthday}
                            city={this.state.userInfo.city}
                            email={this.state.userInfo.email}
                            imagePath={this.state.userInfo.imagePath}
                        />
                        <div className="test-ul">
                            <BottomNavigation className="bottom-navigation">
                                <Link to="/users">
                                    <BottomNavigationItem
                                        label="Пользователи"
                                        icon={GroupIcon}/>
                                </Link>
                                <Link to="/chats">
                                    <BottomNavigationItem
                                        label="Сообщения"
                                        icon={EmailIcon}/>
                                </Link>
                            </BottomNavigation>
                        </div>
                    </Paper>
                </Paper>
            </MuiThemeProvider>
        );
    }
});

export default MainLayout;

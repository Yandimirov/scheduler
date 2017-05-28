import React from 'react';
import {Link} from 'react-router';

import FontIcon from 'material-ui/FontIcon';
import TextField from 'material-ui/TextField';
import FlatButton from 'material-ui/FlatButton';
import axios from 'axios';
import UserLink from './UserLink.js'
import {PATH_API_USERS} from "../paths.js";
import {getPathApiEventUsers} from "../utils";

export default class UserList extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            users: [],
            filteredUsers: [],
            firstName: '',
            lastName: '',
        };

        this.handleFirstNameChange = this.handleFirstNameChange.bind(this);
        this.handleLastNameChange = this.handleLastNameChange.bind(this);
        this.findUsers = this.findUsers.bind(this);
    }

    componentDidMount(){
        if(typeof(this.props.users) == 'undefined') {
            let config = {
                headers: {"x-auth-token": localStorage.getItem('token')}
            };
            axios.get(PATH_API_USERS, config).then(response => {
                this.setState({
                    users: response.data,
                    filteredUsers: response.data
                });
                //alert(response.data.size());
            });
        }else {
            this.setState({
               users: this.props.users
            });
        }
    }

    handleFirstNameChange(event){
        this.setState({
            firstName: event.target.value,
        });
        console.log(event.target.value);
    }

    handleLastNameChange(event){
        this.setState({
            lastName: event.target.value,
        });
    }

    clearFilter = () =>{
        this.setState({
            filteredUsers: this.state.users,
            firstName: '',
            lastName: '',
        })
    };

    findUsers(event){
        let firstName = this.state.firstName;
        let lastName = this.state.lastName;
        let filteredUsers = [];
        let allUsers = this.state.users;

        if(lastName !== '' || firstName !== ''){
            if(lastName === ''){
                filteredUsers = allUsers.filter( (user) => user.firstName.toLowerCase().includes(firstName.toLowerCase()));
            } else if(firstName === ''){
                filteredUsers = allUsers.filter( (user) => user.lastName.toLowerCase().includes(lastName.toLowerCase()));
            } else {
                filteredUsers = allUsers.filter( (user) =>
                    user.lastName.toLowerCase().includes(lastName.toLowerCase()) &&
                        user.firstName.toLowerCase().includes(firstName.toLowerCase())
                );
            }
            this.setState({
                filteredUsers: filteredUsers,
            });
        }
    }

    render() {

        let users = this.state.filteredUsers.map(user =>
            <li key={user.id}>
                <UserLink firstName={user.firstName} lastName={user.lastName} key={user.id} id={user.id}
                          imagePath={user.imagePath}/>
            </li>);

        let str = "Пользователи";
        return (
            <div>
                <TextField
                    hintText="Имя" name="firstName"
                    onChange={this.handleFirstNameChange}
                    value={this.state.firstName}
                    style={{marginLeft: '24px'}}
                />
                <TextField
                    hintText="Фамилия" name="lastName"
                    onChange={this.handleLastNameChange}
                    value={this.state.lastName}
                    style={{marginLeft: '24px'}}
                />
                <FlatButton
                    label="Найти"
                    primary={true}
                    onTouchTap={this.findUsers}
                />
                <FlatButton
                    label="Сброс"
                    primary={true}
                    onTouchTap={this.clearFilter}
                />
                <div className="users">
                    <div className="label"><FontIcon className="material-icons">group</FontIcon> <b>{str}</b></div>
                    <ul className="user-list">
                        {users}
                    </ul>
                </div>
            </div>
        );
    }
};

import React from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import Divider from 'material-ui/Divider';
import Paper from 'material-ui/Paper';
import TextField from 'material-ui/TextField';
import {Link} from 'react-router';
import axios from 'axios';
import {browserHistory} from 'react-router';
import {PATH_AUTH} from '../paths.js';

const LoginForm = React.createClass({

    getInitialState() {
        return {
            username: "",
            password: "",
            errorMessage: ""
        }
    },

    handleChange(e) {
        if (e.target.name == "username") {
            this.setState({username: e.target.value});
            this.setState({errorMessage: ""});
        }
        if (e.target.name == "password") {
            this.setState({password: e.target.value});
            this.setState({errorMessage: ""});
        }
    },

    handleTouchTap: function (e) {
        e.preventDefault();
        this.login();
    },

    login: function () {
        axios.post(PATH_AUTH,
            {
                username: this.state.username,
                password: this.state.password
            })
            .then(response => {
                this.setState({errorMessage: ""});
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('userId', response.data.id);
                browserHistory.push('/');
            })
            .catch(error => {
                if(error.toString().indexOf("401") !== -1){
                    this.setState({errorMessage: "Неправильно введен логин или пароль. Попробуйте еще раз"});
                } else {
                    this.setState({errorMessage: "Отсутствует соединение с сервером аутентификации"});
                }
            });
    },

    render: function () {
        return (
            <div className="login-form">
                <div className="label">Вход</div>
                <Paper zDepth={2}>
                    <form onSubmit={this.handleTouchTap}>
                        <TextField name='username' ref='username' hintText="Login"
                                   style={{width: '360px', margin: '20px'}} onChange={this.handleChange}/>
                        <TextField name='password' ref='password' hintText="Password" type="password"
                                   style={{width: '360px', margin: '20px', marginBottom: '30px', marginTop: '0px'}}
                                   onChange={this.handleChange}/>
                        <div>
                            <RaisedButton
                                primary={true}
                                style={{width: '400px'}}
                                label="Войти"
                                type="Submit"
                            />
                        </div>
                        <div className="error-message">{this.state.errorMessage}</div>
                    </form>
                </Paper>
            </div>
        );
    }
});

export default LoginForm;
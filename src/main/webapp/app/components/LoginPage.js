import React from 'react';
import {Link} from 'react-router';
import Paper from 'material-ui/Paper';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import getMuiTheme from 'material-ui/styles/getMuiTheme';

import Logo from './Logo';
import LoginForm from './LoginForm';
import LoginInfo from './LoginInfo';

import injectTapEventPlugin from 'react-tap-event-plugin';


const muiTheme = getMuiTheme({});
const LoginPage = React.createClass({
    render: function () {
        return (
            <MuiThemeProvider muiTheme={muiTheme}>
                <Paper className="login-page">
                    <Logo />
                    <div className="login-page-main">
                        <div className="login-page-left">
                            <LoginInfo />
                        </div>
                        <div className="login-page-right">
                            {this.props.children}
                        </div>
                    </div>

                </Paper>
            </MuiThemeProvider>
        );
    }
});

export default LoginPage ;
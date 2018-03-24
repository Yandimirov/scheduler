import React from 'react';
import Paper from 'material-ui/Paper';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import getMuiTheme from 'material-ui/styles/getMuiTheme';

import Logo from './Logo';
import LoginInfo from './LoginInfo';


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
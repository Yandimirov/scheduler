import React from 'react';
import {Router, Route, browserHistory, IndexRoute} from 'react-router';

// Layouts
import MainLayout from './components/MainLayout';
import SearchLayout from './components/SearchLayout';

// Pages
import Home from './components/Home';
import UserList from './components/UserList';
import UserProfile from './components/UserProfile';
import LoginForm from './components/LoginForm';
import LoginPage from './components/LoginPage';
import UserEditor from './components/UserEditor';
import ChatList from './components/ChatList';
import ChatMessages from './components/ChatMessages'
import SchedulerComponent from './components/SchedulerComponent';
import EventComponent from './components/EventComponent';

export default (
    <Router history={browserHistory}>
        <Route component={MainLayout}>
            <Route path="/">
                <Route component={Home}>
                    <IndexRoute component={SchedulerComponent}/>
                </Route>
            </Route>
            <Route path="edit" component={UserEditor}/>
            <Route path="users">
                <Route component={SearchLayout}>
                    <IndexRoute component={UserList}/>
                </Route>
                <Route path=":userId" component={UserProfile}/>
            </Route>

            <Route path="event">
                <Route path=":eventId" component={EventComponent}/>
            </Route>

            <Route path="chats">
                <Route component={SearchLayout}>
                    <IndexRoute component={ChatList}/>
                </Route>
                <Route path=":chatId" component={ChatMessages}/>
            </Route>
        </Route>

        <Route component={LoginPage}>
            <Route path="login" component={LoginForm}/>
        </Route>

    </Router>
);

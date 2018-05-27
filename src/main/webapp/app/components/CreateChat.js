import React from 'react';
import Dialog from 'material-ui/Dialog';
import TextField from 'material-ui/TextField';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';
import AutoComplete from 'material-ui/AutoComplete';
import Chip from 'material-ui/Chip';
import jQuery from 'jquery';
import swal from 'sweetalert';
import axios from 'axios';
import {getConfig, getUser} from '../utils.js';
import {PATH_API_USERS, PATH_API_CHAT} from '../paths.js';
import {browserHistory} from 'react-router';

const DATA_SOURCE_CONFIG = {
    text: 'name',
    value: 'id'
};

export default class CreateChat extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            name: "",
            users: [],
            selectedUsers: [],
            open: false,
            currentUser: '',
            chips: [],
            disableInput: true
        };
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleAutoCompleteChange = this.handleAutoCompleteChange.bind(this);
        this.handleUserAdd = this.handleUserAdd.bind(this);
        this.indexOfUser = this.indexOfUser.bind(this);
        this.handleCreateChat = this.handleCreateChat.bind(this);
    }


    componentDidMount(){
        axios.get(PATH_API_USERS, getConfig()).then(response => {
            let responseUsers = response.data;
            this.setState({
                users: responseUsers.map(getUser)
            });
        });
    }

    handleOpen = () => {
        this.setState({open: true});
    };

    handleClose = () => {
        this.setState({
            name: "",
            selectedUsers: [],
            open: false,
            currentUser: '',
            chips: [],
            disableInput: true
        });
    };

    handleNameChange(event) {
        this.setState(
            {
                name: event.target.value
            }
        );
    }

    handleAutoCompleteChange(value){
        this.setState({
           currentUser: value
        });
    }

    indexOfUser(userName){
        const users = this.state.users;
        for(var i = 0; i < users.length; i++){
            //alert(users[i].name);
            if(users[i].name === userName){
                return i;
            }
        }
        return -1;
    }

    handleRequestDelete = (key) => {
        let selectedUsers = this.state.selectedUsers;
        let users = this.state.users;
        let chipData = this.state.chips;
        const chipToDelete = chipData.map((chip) => chip.key).indexOf(key);
        chipData.splice(chipToDelete, 1);

        let idx = -1;
        for(let i = 0; i < selectedUsers.length; i++){
            if(selectedUsers[i].id == key){
                idx = i;
            }
        }
        if(idx >= 0){
            users.push(selectedUsers[idx]);
            selectedUsers.splice(idx, 1);

            if (selectedUsers.length < 2){
                this.setState({
                    name: '',
                    disableInput: true
                });
            } else {
                this.setState({
                    name: 'Новая переписка',
                    disableInput: false
                });
            }
            //console.log(selectedUsers);
        }

        this.setState({
            selectedUsers: selectedUsers,
            chips: chipData,
            users: users
        });
    };

    handleUserAdd(event){
        //alert(this.state.currentUser);
        let userIdx = this.indexOfUser(this.state.currentUser);
        let selectedUsers = this.state.selectedUsers;
        let users = this.state.users;
        if(userIdx != -1){
            selectedUsers.push(users[userIdx]);
            if (selectedUsers.length < 2){
                this.setState({
                    name: this.state.currentUser,
                    disableInput: true
                })
            } else {
                this.setState({
                    name: 'Новая переписка',
                    disableInput: false
                })
            }
            users.splice(userIdx, 1);
            let chips = this.state.chips;
            chips.push(<Chip
                onRequestDelete={() => this.handleRequestDelete(selectedUsers[selectedUsers.length - 1].id)}
                key={selectedUsers[selectedUsers.length - 1].id}
            >{selectedUsers[selectedUsers.length - 1].name}</Chip>);
            this.setState({
                chips: chips,
                selectedUsers: selectedUsers,
                users: users,
                currentUser: ''
            });
            this.refs['autoCompleteUserRef'].setState({searchText:''});
        }
    }

    handleCreateChat(event){
        let userIds = [];
        userIds.push(localStorage.getItem('userId'));
        let users = this.state.selectedUsers;
        for(let i = 0; i < users.length; i++){
            userIds.push(users[i].id);
        }
        let chatDto = {
            name: this.state.name,
            users: userIds,
        };
        console.log(chatDto);
        let trimmedName = jQuery.trim(chatDto.name);
        if(trimmedName === '' || trimmedName === null){
            swal("Введите непустое название переписки");
        }else {
            axios.post(
                PATH_API_CHAT,
                chatDto,
                getConfig()
            ).then( response =>{
                console.log(response.data);
                browserHistory.push('/chats/' + response.data.chat.id);
            });
        }
    }

    render(){
        const actions = [
            <FlatButton
                label="Отмена"
                primary={true}
                onTouchTap={this.handleClose}
            />,
            <FlatButton
                label="Создать"
                primary={true}
                keyboardFocused={true}
                onTouchTap={this.handleCreateChat}
            />,
        ];

        return(
            <div>
                <FlatButton label="Начать переписку" onTouchTap={this.handleOpen}/>
                <Dialog
                    title="Начать переписку"
                    actions={actions}
                    open={this.state.open}
                    onRequestClose={this.handleClose}
                    autoScrollBodyContent={true}
                >
                    <div id="create_chat" className="inline">
                        <TextField hintText="Название" name="name"
                                   onChange={this.handleNameChange}
                                   value={this.state.name}
                                   style={{marginLeft: '24px'}}
                                   disabled={this.state.disableInput}
                        /><br/>
                        <AutoComplete
                            dataSource={this.state.users}
                            name="autoCompleteUser"
                            floatingLabelText="Введите пользователя"
                            ref={'autoCompleteUserRef'}
                            filter={AutoComplete.fuzzyFilter}
                            dataSourceConfig={DATA_SOURCE_CONFIG}
                            onUpdateInput={this.handleAutoCompleteChange}
                            style={{marginLeft: '24px'}}
                        /><br/>
                        <RaisedButton
                            id="user-add"
                            backgroundColor="000000"
                            label="Добавить пользователя"
                            primary={true}
                            onTouchTap={this.handleUserAdd}
                            style={{marginLeft: '24px'}}
                        />

                    </div>
                    <div id="selected_users" className="inline">
                        {this.state.chips}
                    </div>
                </Dialog>
            </div>
        );
    }
}
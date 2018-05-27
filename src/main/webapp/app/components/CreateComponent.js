import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import Checkbox from 'material-ui/Checkbox';
import TextField from 'material-ui/TextField';
import DatePicker from 'material-ui/DatePicker';
import swal from 'sweetalert';
import jQuery from 'jquery';
import TimePicker from 'material-ui/TimePicker';
import PlacesAutocomplete, {geocodeByAddress} from 'react-places-autocomplete';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import {PATH_API_EVENT, PATH_API_USERS} from '../paths';
import {getConfig, getUser} from '../utils.js';
import axios from 'axios';
import {AutoComplete, Chip, RaisedButton} from "material-ui";
import moment from "moment";

const styles = {
    radioButton: {
        marginTop: 16,
    },
};

const DATA_SOURCE_CONFIG = {
    text: 'name',
    value: 'id'
};

export default class CreateComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: "",
            description: "",
            place: {
                name: "",
                id: "",
                lat: "",
                lng: "",
            },
            chips: [],
            currentUser: "",
            selectedUsers: [],
            users: [],
            startDate: null,
            startTime: null,
            endDate: null,
            endTime: null,
            repeatEnd: null,
            repeat: false,
            open: false,
            freq: null,
            freqValue: null
        };
        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleStartDateChange = this.handleStartDateChange.bind(this);
        this.handleEndDateChange = this.handleEndDateChange.bind(this);
        this.handleStartTimeChange = this.handleStartTimeChange.bind(this);
        this.handleEndTimeChange = this.handleEndTimeChange.bind(this);
        this.handlePlaceChange = this.handlePlaceChange.bind(this);
        this.handleRepeatChange = this.handleRepeatChange.bind(this);
        this.handleRepeatEndChange = this.handleRepeatEndChange.bind(this);
        this.handleFreqChange = this.handleFreqChange.bind(this);
        this.handleFreqValueChange = this.handleFreqValueChange.bind(this);
        this.handleCreate = this.handleCreate.bind(this);
        this.clearState = this.clearState.bind(this);
        this.handleSelectPlace = this.handleSelectPlace.bind(this);
        this.handleAutoCompleteChange = this.handleAutoCompleteChange.bind(this);
        this.handleUserAdd = this.handleUserAdd.bind(this);
        this.indexOfUser = this.indexOfUser.bind(this);
    }

    componentDidMount(){
        axios.get(PATH_API_USERS, getConfig()).then(response => {
            let responseUsers = response.data;
            this.setState({
                users: responseUsers.map(getUser)
            });
        });
    }

    indexOfUser(userName){
        const users = this.state.users;
        for(var i = 0; i < users.length; i++){
            if(users[i].name === userName){
                return i;
            }
        }
        return -1;
    }

    handleUserAdd(event) {
        let userIdx = this.indexOfUser(this.state.currentUser);
        let selectedUsers = this.state.selectedUsers;
        let users = this.state.users;
        if(userIdx != -1){
            selectedUsers.push(users[userIdx]);
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

    handleAutoCompleteChange(value){
        this.setState({
            currentUser: value
        });
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
        }

        this.setState({
            selectedUsers: selectedUsers,
            chips: chipData,
            users: users
        });
    };

    clearState(){
        this.setState({
            name: "",
            description: "",
            place: {
                name: "",
                id: "",
                lat: "",
                lng: "",
            },
            chips: [],
            selectedUsers: [],
            currentUser: "",
            users: [],
            startDate: null,
            startTime: null,
            endDate: null,
            endTime: null,
            repeatEnd: null,
            repeat: false,
            open: false,
            freq: null,
            freqValue: null
        });
    };

    handleDescriptionChange(event) {
        this.setState(
            {
                description: event.target.value
            }
        );
    }

    handlePlaceChange(event){
        this.setState({
            place: {
                name: event,
                id: '',
                lat: '',
                lng: '',
            }
        });
    }

    handleSelectPlace(event, placeId){
        geocodeByAddress(event,  (err, latLng)  => {
            if(!err){
                console.log(`Yay! Got latitude and longitude for ${event}`, latLng);
                this.setState({
                    place: {
                        name: event,
                        id: placeId,
                        lat: latLng.lat,
                        lng: latLng.lng,
                    }
                });
            } else {
                this.setState({
                    place: {
                        name: event,
                        id: "",
                        lat: "",
                        lng: "",
                    }
                });
            }
        });
    }

    handleNameChange(event) {
        this.setState(
            {
                name: event.target.value
            }
        );
    }

    handleStartDateChange(event, date) {
        this.setState(
            {
                startDate: date
            }
        );
    }

    handleEndDateChange(event, date) {
        this.setState(
            {
                endDate: date
            }
        );
    }

    handleStartTimeChange(event, date) {
        this.setState(
            {
                startTime: date
            }
        );
    }

    handleEndTimeChange(event, date) {
        this.setState(
            {
                endTime: date
            }
        );
    }

    handleOpen = () => {
        this.setState({open: true});
    };

    handleClose = () => {
        this.setState({open: false});
        this.clearState();
    };

    handleRepeatChange(event, isChecked){
        this.setState({
           repeat:  isChecked
        });
    };

    handleRepeatEndChange(event, date){
        this.setState({
           repeatEnd: date
        });
    };

    handleFreqChange(event, index, value){
        this.setState({
            freq: value
        });
    };

    handleFreqValueChange(event, index, value){
        this.setState({
            freqValue: value
        });
        console.log(value);
    }

    handleCreate(event){
        const state = this.state;
        let repeatsDto = null;
        let flag = true;
        let now = new Date();
        if(state.repeat){
            if(state.repeatEnd < now || state.repeatEnd === null){
                flag = false;
            } else {
                repeatsDto = {
                    value: state.freqValue,
                    freq: state.freq,
                    until: state.repeatEnd
                };
            }
        }
        let start;
        let end;

        if(state.startDate === null || state.startTime === null){
            swal("Введите дату начала");
        } else {
             start = new Date(state.startDate.getFullYear(), state.startDate.getMonth(), state.startDate.getDate(),
                state.startTime.getHours(), state.startTime.getMinutes(), state.startTime.getSeconds()
            );
        }
        if(state.endDate === null || state.endTime === null){
            swal("Введите дату окончания");
        } else {
            end = new Date(state.endDate.getFullYear(), state.endDate.getMonth(), state.endDate.getDate(),
                state.endTime.getHours(), state.endTime.getMinutes(), state.endTime.getSeconds()
            );
        }
        if(end < start || end < now || start < now){
            swal("Введите корректные даты события");
        } else if(jQuery.trim(this.state.name) === '' || this.state.name === null){
            swal("Введите непустое название собыия");
        } else {
            if(start && end){
                if(flag){
                    let userIds = this.state.selectedUsers.map(user => user.id);
                    let eventDto = {
                        name: state.name,
                        description: state.description,
                        place: state.place,
                        repeats: repeatsDto,
                        startDate: start,
                        endDate: end,
                        userIds: userIds
                    };
                    axios.post(
                        PATH_API_EVENT,
                        eventDto,
                        getConfig()
                    ).then(response => {
                        this.props.updateEvents();
                    });
                    this.clearState();
                } else {
                    swal("Введите корректную дату окончания повторов");
                }

            }
        }
    }

    render() {
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
                onTouchTap={this.handleCreate}
            />,
        ];

        return (
            <div>
                <FlatButton style={this.props.style} label={this.props.label} onTouchTap={this.handleOpen}/>
                <Dialog
                    title={this.props.label}
                    actions={actions}
                    modal={false}
                    open={this.state.open}
                    onRequestClose={this.handleClose}
                    autoScrollBodyContent={true}
                >
                    <div id="create_form" className="inline">
                        <TextField hintText="Название" name="name"
                                   onChange={this.handleNameChange}
                                   value={this.state.name}
                                   style={{marginLeft: '24px'}}/>
                        <PlacesAutocomplete
                            styles = {
                            {
                                root: {
                                    marginLeft: '24px',
                                    zIndex: '200',
                                    fontSize: '16px',
                                    color: 'black',
                                    border: 'hidden',
                                },
                                input: {
                                    paddingTop: '20',
                                    paddingLeft: '0',
                                    border: 'hidden',
                                    fontSize: '16px',
                                    color: 'black',
                                    transition: 'height 200ms cubic-bezier(0.23, 1, 0.32, 1) 0ms',
                                    fontFamily: 'Roboto'
                                },
                                autocompleteContainer: {
                                    border: 'hidden',
                                    fontSize: '16px',

                                },
                                autocompleteItem: {
                                    fontSize: '16px',
                                    border: 'hidden',
                                    color: 'black',
                                },
                                autocompleteItemActive: { color: 'blue' }
                            }
                            }
                            value={this.state.place.name}
                            onChange={this.handlePlaceChange}
                            onSelect={this.handleSelectPlace}
                            placeholder="Местоположение"
                        />
                        <TextField hintText="Описание" name="description"
                                   onChange = {this.handleDescriptionChange}
                                   value = {this.state.description}
                                   style = {{marginLeft: '24px'}}
                                   multiLine = {true}
                                   rows = {2}
                                   rowsMax = {5}
                        /><br/>
                        <DatePicker hintText = "Дата начала"
                                     value = {this.state.startDate}
                                     container = "inline"
                                     style = {{marginLeft: '24px'}}
                                     okLabel = "Ок"
                                     cancelLabel = "Отмена"
                                     name = "startDate"
                                     onChange = {this.handleStartDateChange}
                        />
                        <TimePicker value = {this.state.startTime}
                                    hintText="Время начала"
                                    okLabel="Ок"
                                    cancelLabel="Отмена"
                                    format="24hr"
                                    style = {{marginLeft: '24px'}}
                                    onChange = {this.handleStartTimeChange}
                        />
                        <DatePicker hintText="Дата окончания"
                                    value={this.state.endDate}
                                    container = "inline"
                                    style = {{marginLeft: '24px'}}
                                    okLabel = "Ок"
                                    cancelLabel = "Отмена"
                                    name = "entDate"
                                    onChange = {this.handleEndDateChange}
                        />
                        <TimePicker value = {this.state.endTime}
                                    hintText = "Время окончания"
                                    okLabel = "Ок"
                                    cancelLabel = "Отмена"
                                    format = "24hr"
                                    style = {{marginLeft: '24px'}}
                                    onChange = {this.handleEndTimeChange}
                        />
                    </div>
                    <div id="create_form2" className="inline">
                        <div>
                            <Checkbox
                                label="Повтор"
                                onCheck={this.handleRepeatChange}
                                style={{
                                    marginLeft: '24px'
                                }}
                            />

                            <SelectField
                                disabled={!this.state.repeat}
                                floatingLabelText="Через"
                                value={this.state.freqValue}
                                onChange={this.handleFreqValueChange}
                                style = {{
                                    marginLeft: '24px',
                                    width: '100px',
                                }}
                            >
                                <MenuItem value={1} primaryText="1" />
                                <MenuItem value={2} primaryText="2" />
                                <MenuItem value={3} primaryText="3" />
                            </SelectField>
                            <SelectField
                                disabled={!this.state.repeat}
                                value={this.state.freq}
                                onChange={this.handleFreqChange}
                                floatingLabelText="Частота"
                                style = {{
                                    marginLeft: '24px',
                                    width: '100px',
                                }}
                            >
                                <MenuItem value={'DAY'} primaryText="День" />
                                <MenuItem value={'WEEK'} primaryText="Неделя" />
                                <MenuItem value={'MONTH'} primaryText="Месяц" />
                                <MenuItem value={'YEAR'} primaryText="Год" />
                            </SelectField>
                            <DatePicker hintText="До"
                                        disabled={!this.state.repeat}
                                        value={this.state.repeatEnd}
                                        container = "inline"
                                        style = {{marginLeft: '24px'}}
                                        okLabel = "Ок"
                                        cancelLabel = "Отмена"
                                        name = "entDate"
                                        onChange = {this.handleRepeatEndChange}
                            />
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
                            /><br/>
                            <div id="selected_users">
                                {this.state.chips}
                            </div>
                        </div>
                    </div>
                </Dialog>
            </div>
        );
    }
}
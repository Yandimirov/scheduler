import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import DatePicker from 'material-ui/DatePicker';
import TimePicker from 'material-ui/TimePicker';
import PlacesAutocomplete from 'react-places-autocomplete';
import TextField from 'material-ui/TextField';
import { geocodeByAddress} from 'react-places-autocomplete';
import {getConfig, getPathApiEvent} from '../utils.js';
import axios from 'axios';
import {PATH_API_EVENT} from '../paths';

export default class UpdateComponent extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            name: "",
            description: "",
            place: {
                name: "",
                id: "",
                lat: "",
                lon: "",
            },
            startDate: null,
            startTime: null,
            endDate: null,
            endTime: null,
            open: false,
            info: 0,
        };

        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleStartDateChange = this.handleStartDateChange.bind(this);
        this.handleEndDateChange = this.handleEndDateChange.bind(this);
        this.handleStartTimeChange = this.handleStartTimeChange.bind(this);
        this.handleEndTimeChange = this.handleEndTimeChange.bind(this);
        this.handlePlaceChange = this.handlePlaceChange.bind(this);
        this.handleSelectPlace = this.handleSelectPlace.bind(this);
        this.clearState = this.clearState.bind(this);
        this.handleUpdateEvent = this.handleUpdateEvent.bind(this);
    }

    componentDidMount(){
        axios.get(
            getPathApiEvent(this.props.event),
            getConfig()
        ).then(response => {
            let event = response.data;
            console.log(event);
            this.setState({
                name: event.info.name,
                description: event.info.description,
                place: event.info.place,
                startDate: new Date(event.startDate),
                startTime: new Date(event.startDate),
                endDate: new Date(event.endDate),
                endTime: new Date(event.endDate),
                info: event.info.id
            });
        });
    }

    handleUpdateEvent(){
        let state = this.state;
        let start = new Date(state.startDate.getFullYear(), state.startDate.getMonth(), state.startDate.getDate(),
            state.startTime.getHours(), state.startTime.getMinutes(), state.startTime.getSeconds()
        );
        let end = new Date(state.endDate.getFullYear(), state.endDate.getMonth(), state.endDate.getDate(),
            state.endTime.getHours(), state.endTime.getMinutes(), state.endTime.getSeconds()
        );

        let event = {
            id: this.props.event,
            startDate: start,
            endDate: end,
            info: {
                description: this.state.description,
                name: this.state.name,
                id: this.state.info,
                place: this.state.place
            }
        };
        console.log(event);
        axios.put(
            PATH_API_EVENT,
            event,
            getConfig()
        ).then(response => {
            this.props.updateParent(response);
        });
        this.handleClose();
    }

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
                lon: '',
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
                        lon: latLng.lng,
                    }
                });
            } else {
                this.setState({
                    place: {
                        name: event,
                        id: "",
                        lat: "",
                        lon: "",
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
            startDate: null,
            startTime: null,
            endDate: null,
            endTime: null,
        });
    };

    render(){
        const actions = [
            <FlatButton
                label="Отмена"
                primary={true}
                onTouchTap={this.handleClose}
            />,
            <FlatButton
                label="Обновить"
                primary={true}
                keyboardFocused={true}
                onTouchTap={this.handleUpdateEvent}
            />,
        ];

        return (
            <div>
                <FlatButton
                    label="Изменить"
                    onTouchTap={this.handleOpen}
                    primary={true}
                    style={this.props.style}
                />
                <Dialog
                    title="Изменить событие"
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
                            value={this.state.place ? this.state.place.name : ''}
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
                        < DatePicker hintText = "Дата начала"
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
                </Dialog>
            </div>
        )
    }

}
import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import Checkbox from 'material-ui/Checkbox';
import FontIcon from 'material-ui/FontIcon';
import {RadioButton, RadioButtonGroup} from 'material-ui/RadioButton';
import TextField from 'material-ui/TextField';
import DatePicker from 'material-ui/DatePicker';
import swal from 'sweetalert';
import jQuery from 'jquery';
import TimePicker from 'material-ui/TimePicker';
import PlacesAutocomplete from 'react-places-autocomplete';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import { geocodeByAddress} from 'react-places-autocomplete';
import {PATH_API_EVENT} from '../paths';
import {getConfig} from '../utils.js';
import axios from 'axios';
import ReactDOM from 'react-dom';

const styles = {
    radioButton: {
        marginTop: 16,
    },
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
    }

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
        //console.log(geocodeByAddress(event));
        //console.log(event.target.value);
        //console.log(event);
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
        console.log(value);
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
                    let eventDto = {
                        name: state.name,
                        description: state.description,
                        place: state.place,
                        repeats: repeatsDto,
                        startDate: start,
                        endDate: end
                    };
                    axios.post(
                        PATH_API_EVENT,
                        eventDto,
                        getConfig()
                    ).then(response => {
                        console.log(response.data);
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
                        </div>

                    </div>
                </Dialog>
            </div>
        );
    }
}
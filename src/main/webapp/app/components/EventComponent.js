/**
 * Created by Mikhail on 20.04.2017.
 */
import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';
import DatePicker from 'material-ui/DatePicker';
import TimePicker from 'material-ui/TimePicker';
import PlacesAutocomplete from 'react-places-autocomplete';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import {geocodeByAddress} from 'react-places-autocomplete';
import {PATH_API_EVENT} from '../paths';
import {getConfig, getPathApiUserRole, getPathApiEvent, getPathApiUserEvent} from '../utils.js';
import axios from 'axios';
import moment from 'moment';
import {ExtendedGoogleMap} from './ExtendedGoogleMap';
import MapContainer from './MapContainer';
import swal from 'sweetalert';
import {browserHistory, Link} from 'react-router';
import UserLink from './UserLink';
import {getPathApiEventUsers} from "../utils";
import FontIcon from 'material-ui/FontIcon';
import UpdateComponent from './UpdateComponent';
import EventHistory from "./EventHistory";

export default class EventComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            event: {
                id: '',
                startDate: '',
                endDate: '',
                createdAt: '',
                info: {
                    id: '',
                    name: '',
                    description: '',
                    createdBy: {
                        id: '',
                        firstName: '',
                        lastName: ''
                    },
                    place: {
                        id: '',
                        lat: '',
                        lon: '',
                        name: ''
                    }
                },

            },
            users: [],
            userList: '',
            open: false,
            moderFunc: false,
            subscribed: false,
            waited: false,
            subscribeDisabled: false,
            notification1: {
                date: null,
                time: null
            },
            notification2: {
                date: null,
                time: null
            },
            notification3: {
                date: null,
                time: null
            },
        };

        this.handleChangeEvent = this.handleChangeEvent.bind(this);
        this.handleSubcribeEvent = this.handleSubcribeEvent.bind(this);
        this.handleDeleteEvent = this.handleDeleteEvent.bind(this);
        this.handleRejectEvent = this.handleRejectEvent.bind(this);
        this.handleCreate = this.handleCreate.bind(this);
        this.handleTime1Change = this.handleTime1Change.bind(this);
        this.handleTime2Change = this.handleTime2Change.bind(this);
        this.handleDate1Change = this.handleDate1Change.bind(this);
        this.handleDate2Change = this.handleDate2Change.bind(this);
        this.handleApproveEvent = this.handleApproveEvent.bind(this);
        this.updateEvent = this.updateEvent.bind(this);
    }

    handleChangeEvent(event) {

    };

    handleApproveEvent(event){
        let calendarEvent = this.state.event;
        calendarEvent.type = "ACCEPTED";
        axios.put(
            PATH_API_EVENT,
            calendarEvent,
            getConfig()
        ).then(response => {
            this.setState({
                event: response.data
            })
        });
    };

    handleRejectEvent(event){
        let calendarEvent = this.state.event;
        axios.post(
            getPathApiUserEvent(calendarEvent.id) + "/decline",
            null,
            getConfig()
        ).then(response => {browserHistory.push('/');});
    };

    handleDeleteEvent(event) {
        axios.delete(
            getPathApiEvent(this.props.params.eventId),
            getConfig()
        ).then(response =>{
            if(response.data === true){
                swal({
                        title: "Событие успешно удалено!",
                        text: "Нажмите 'ок' для перенаправления на главную страницу.",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: "Ок",
                        closeOnConfirm: true,
                    },
                    function(){
                        browserHistory.push('/');
                    });
            } else {
                swal("Что-то пошло не так!");
            }

        });
    };

    handleTime1Change(event, date){
        this.setState({
            notification1: {
                date: this.state.notification1.date,
                time: date
            }
        });
    };

    handleTime2Change(event, date){
        this.setState({
            notification2: {
                date: this.state.notification2.date,
                time: date
            }
        });
    };

    handleDate1Change(event, date){
        this.setState({
            notification1: {
                time: this.state.notification1.time,
                date: date
            }
        });
    };

    handleDate2Change(event, date){
        this.setState({
            notification2: {
                time: this.state.notification2.time,
                date: date
            }
        });
    };

    handleClose = () => {
        this.setState({open: false});
    };

    handleSubcribeEvent(event) {
        if(!this.state.subscribed){
            this.setState({
                open: true,
                waited: false
            });
        } else {
            // TODO add check fo nulls
            axios.delete(
                getPathApiUserEvent(this.props.params.eventId),
                getConfig()
            ).then(response =>{
                if(response.data){
                    swal("Отписка прошла успешно!");
                    this.setState({
                        subscribed: false,
                    });
                } else {
                    swal("Что-то пошло не так!");
                }
                axios.get(
                    getPathApiEventUsers(this.props.params.eventId),
                    getConfig()
                ).then(response => {
                    this.setState({
                        users: response.data,
                    });
                });
            });
        }
    };

    handleCreate(event){
        let notifications = [];
        let notification1 = this.state.notification1;
        let flag1 = false;
        let flag2 = false;
        let now = new Date();
        if(notification1.date != null && notification1.time != null){
            notification1 = new Date(notification1.date.getFullYear(), notification1.date.getMonth(), notification1.date.getDate(),
                notification1.time.getHours(), notification1.time.getMinutes(), notification1.time.getSeconds()
            );
            if(notification1 < now || notification1 > this.state.event.startDate) {
                flag1 = true;
            } else{
                notifications.push(notification1);
            }
        }
        let notification2 = this.state.notification2;
        if(notification2.date != null && notification2.time != null){
            notification2 = new Date(notification2.date.getFullYear(), notification2.date.getMonth(), notification2.date.getDate(),
                notification2.time.getHours(), notification2.time.getMinutes(), notification2.time.getSeconds()
            );
            if(notification2 < now || notification2 > this.state.event.startDate) {
                flag2 = true;
            } else{
                notifications.push(notification2);
            }
        }
        let eventDto = {
            id: this.props.params.eventId,
            notifications: notifications,
        };
        if(flag1 || flag2){
            swal("Введите корректные даты уведомлений");
        } else {
            axios.post(
                getPathApiUserEvent(eventDto.id),
                eventDto,
                getConfig()
            ).then(response =>{
                this.setState({
                    subscribed: true,
                    open: false
                });
                swal("Успешно");
                axios.get(
                    getPathApiEventUsers(this.props.params.eventId),
                    getConfig()
                ).then(response => {
                    this.setState({
                        users: response.data,
                    });
                });
            });
        }
    };

    updateEvent(){
        axios.get(
            getPathApiEvent(this.props.params.eventId),
            getConfig()
        ).then(response => {
            this.setState({
                event: response.data
            });
            console.log("Update event!!! event: " + response.data);
            if(this.state.event.endDate < new Date()){
                this.setState({
                    subscribeDisabled: true
                });
            } else {
                this.setState({
                    subscribeDisabled: false
                });
            }
        });
    }

    componentDidMount() {
        axios.get(
            getPathApiUserRole(),
            getConfig()
        ).then(response => {
            if (response.data == "MODERATOR") {
                this.setState({
                    moderFunc: true
                });
            } else {
                this.setState({
                    moderFunc: false
                });
            }
        });


        axios.get(
            getPathApiEventUsers(this.props.params.eventId),
            getConfig()
        ).then(response => {
            this.setState({
                users: response.data,
            });
        });

        axios.get(
            getPathApiUserEvent(this.props.params.eventId),
            getConfig()
        ).then(response => {
            if(response.data === null){
                this.setState({
                   subscribed: false
                });
            } else {
                let userEvent = response.data;
                if (userEvent.status === 'WAITED') {
                    this.setState({
                        subscribed: false,
                        waited: true
                    });
                } else {
                    this.setState({
                        subscribed: true,
                        waited: false
                    });
                }
            }
        });

        axios.get(
            getPathApiEvent(this.props.params.eventId),
            getConfig()
        ).then(response => {
            this.setState({
                event: response.data
            });
            if(this.state.event.endDate < new Date()){
                this.setState({
                    subscribeDisabled: true
                });
            } else {
                this.setState({
                    subscribeDisabled: false
                });
            }
        });
    };

    render() {
        let moderStyles = null;
        let userId = localStorage.getItem('userId');
        if (this.state.event.info.createdBy.id == userId) {
            moderStyles = {
                display: 'inline'
            };
        } else {
            moderStyles = {
                display: 'none'
            };
        }

        moment.locale('ru');

        let label = '';

        if(this.state.subscribed) {
            label = 'Отписаться';
        } else {
            label = 'Подписаться';
        }

        let actions = [

            <UpdateComponent
                event={this.props.params.eventId}
                style={moderStyles}
                updateParent={this.updateEvent}
            />,
            <FlatButton
                label="Удалить"
                primary={true}
                onTouchTap={this.handleDeleteEvent}
                style={moderStyles}
            />,
            <FlatButton
                label={label}
                primary={true}
                onTouchTap={this.handleSubcribeEvent}
                disabled={this.state.subscribeDisabled}
            />,
        ];

        if(this.state.waited){
            actions.push(<FlatButton
                label="Отклонить"
                primary={true}
                onTouchTap={this.handleRejectEvent}
            />);
        }

        let notifActions = [
            <FlatButton
                label="Отмена"
                primary={true}
                onTouchTap={this.handleClose}
            />,
            <FlatButton
                label="Подписаться"
                primary={true}
                keyboardFocused={true}
                onTouchTap={this.handleCreate}
            />,
        ];

        const event = this.state.event;

        let mapComponent = '';
        if(event.info.place.id != ''){
            mapComponent = <MapContainer width={1300} height={300} event={this.props.params.eventId} center={{lat: event.info.place.lat, lng: event.info.place.lon}}/>;
        }

        let userList = '';
        let str = '';
        let userDiv = '';
        if(this.state.users.length > 0){
            userList = this.state.users.map(user =>
                <li key={user.id}>
                    <UserLink firstName={user.firstName} lastName={user.lastName} key={user.id} id={user.id}
                              imagePath={user.imagePath}/>
                </li>);
            str = "Участники";
            userDiv = <div className="users">
                <div className="label"><FontIcon className="material-icons">group</FontIcon> <b>{str}</b></div>
                <ul className="user-list">
                    {userList}
                </ul>
            </div>;
        }
        let userCount = <p>Количество участников: {this.state.users.length}</p>;

        return (
            <div>
                <div className="event-notifications">
                    <Dialog
                        title="Напоминания"
                        actions={notifActions}
                        modal={false}
                        open={this.state.open}
                        onRequestClose={this.handleClose}
                        autoScrollBodyContent={true}
                    >
                        <div>
                            <p>Первое напоминание</p><br/>
                            <DatePicker hintText = "Дата"
                                        value = {this.state.notification1.date}
                                        container = "inline"
                                        style = {{marginLeft: '24px'}}
                                        okLabel = "Ок"
                                        cancelLabel = "Отмена"
                                        name = "startDate"
                                        onChange={this.handleDate1Change}
                            />
                            <TimePicker value = {this.state.notification1.time}
                                        hintText="Время"
                                        okLabel="Ок"
                                        cancelLabel="Отмена"
                                        format="24hr"
                                        style = {{marginLeft: '24px'}}
                                        onChange={this.handleTime1Change}
                            />
                        </div>
                        <div>
                            <p>Второе напоминание</p><br/>
                            <DatePicker hintText = "Дата"
                                        value = {this.state.notification2.date}
                                        container = "inline"
                                        style = {{marginLeft: '24px'}}
                                        okLabel = "Ок"
                                        cancelLabel = "Отмена"
                                        name = "startDate"
                                        onChange={this.handleDate2Change}
                            />
                            <TimePicker value = {this.state.notification2.time}
                                        hintText="Время"
                                        okLabel="Ок"
                                        cancelLabel="Отмена"
                                        format="24hr"
                                        style = {{marginLeft: '24px'}}
                                        onChange={this.handleTime2Change}
                            />
                        </div>
                    </Dialog>
                </div>
                <div id="event-info">
                    <div className="event-info-title">
                        <h2>{event.info.name}</h2>
                    </div>
                        <p><b>Дата создания</b>: {moment(event.createdAt).format('LLL')}</p>
                        <p><b>Начало события</b>: {moment(event.startDate).format('LLL')}</p>
                        <p><b>Конец события</b>: {moment(event.endDate).format('LLL')}</p>
                        <p><b>Создатель события</b>: <Link to={"/users/" + event.info.createdBy.id}> {event.info.createdBy.firstName} {event.info.createdBy.lastName}</Link></p>
                        <p><b>Описание</b>: {event.info.description}</p>
                        <p><b>Местоположение:</b> {event.info.place.name}</p>
                    <div>
                    </div>
                </div>
                <div className="event-info-map-container">
                    {mapComponent}
                </div>
                <div className="event-info-funcs" style={{display: 'flex', flexDirection: 'row', alignItems: 'center'}}>
                    {actions}
                </div>
                <div style={{display: 'flex', flexDirection: 'row', alignItems: 'center'}}>
                    <EventHistory style={{display: 'inline'}} eventId={this.props.params.eventId}/>
                </div>
                <div>
                    {userCount}
                </div>
                <div>
                    {userDiv}
                </div>
            </div>
        );
    }
}
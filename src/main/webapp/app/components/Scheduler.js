import {fullCalendar} from 'fullcalendar';
import 'fullcalendar/dist/locale-all.js';
import React from 'react';
import jQuery from 'jquery';
import swal from 'sweetalert';
import 'swal-forms';
import {UserProfileMessage} from './UserProfileMessage.js';
import {PATH_API_EVENT, PATH_API_BIRTHDAY} from '../paths';
import {getConfig} from '../utils.js';
import {mapEvents} from '../utils.js';
import axios from 'axios';
import CreateComponent from './CreateComponent.js';
import {getPathApiUserRole, getPathApiUserEvents, getEventByType} from '../utils.js';
import {browserHistory} from 'react-router';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import styles from 'fullcalendar';


class Scheduler extends React.Component {
    constructor(props) {
        super(props);
        this.state= {
            createFormDisplay: 'none',
            events: [],
            birthdays: [],
            hideCreate: true,
            value: 1
        };

        this.updateEvents = this.updateEvents.bind(this);
        this.handleSelect = this.handleSelect.bind(this);
    }


    handleSelect(event, key, payload){
        this.setState({
            value: payload
        });
    }

    render() {
        let styles = {display: 'block', marginLeft: 170};
        let label = "";
        let waitedStyle = null;
        if(!this.state.hideCreate){
            label = "Создать событие";
            waitedStyle = {display: "block"};
        } else {
            label = "Предложить событие";
            waitedStyle = {display: "none"};
        }

        let myPage = '';
        if(typeof(this.props.user) === 'undefined'){
            myPage = <div style={{display: 'flex', flexDirection: 'row', marginLeft: 170, marginRight: 50}}>
                <CreateComponent updateEvents={this.updateEvents} label={label} style={{marginTop: 10}}/>
                <SelectField value={this.state.value} onChange={this.handleSelect} >
                    <MenuItem value={1} label="Подтвержденные" primaryText="Подтвержденные" />
                    <MenuItem value={2} label="Участник" primaryText="Участник" />
                    <MenuItem value={3} label="Дни рождения" primaryText="Дни рождения" />
                    <MenuItem value={4} label="Непроверенные" primaryText="Непроверенные" style={waitedStyle}/>
                </SelectField>
            </div>;
        }

        return (
            <div>
                {myPage}
                <div id="calendar"></div>
            </div>
        );
    }



    updateEvents(){
        if(typeof(this.props.user) === 'undefined') {
            axios.get(
                PATH_API_EVENT + "/calendar",
                getConfig()
            ).then( response =>{
                this.setState({
                    events: mapEvents(response.data)
                });
            });
        } else {
            axios.get(
                getPathApiUserEvents(this.props.user),
                getConfig()
            ).then( response => {
                this.setState({
                    events: mapEvents(response.data)
                });
            });
        }
    }

    componentDidUpdate(prevProps, prevState){
        let events = this.state.events;
        let birthdays = this.state.birthdays;
        jQuery('#calendar').fullCalendar('removeEvents');
        if(this.state.value == 1){
            jQuery('#calendar').fullCalendar('renderEvents', events, true);
            jQuery('#calendar').fullCalendar('renderEvents', birthdays, true);
        } else if (this.state.value == 3) {
            jQuery('#calendar').fullCalendar('renderEvents', birthdays, true);
        } else if (this.state.value == 2){
            axios.get(
                getPathApiUserEvents(localStorage.getItem('userId')),
                getConfig()
            ).then( response => {
                jQuery('#calendar').fullCalendar('renderEvents', mapEvents(response.data), true);
            });
        } else if (this.state.value == 4){
            axios.get(
                getEventByType("WAITED"),
                getConfig()
            ).then( response => {
                jQuery('#calendar').fullCalendar('renderEvents', mapEvents(response.data), true);
            });
        }
    }

    componentDidMount() {
        if(typeof(this.props.user) === 'undefined'){
            axios.get(
                PATH_API_EVENT + "/calendar",
                getConfig()
            ).then( response =>{
                this.setState({
                    events: mapEvents(response.data)
                });
            });
            axios.get(
                getPathApiUserRole(),
                getConfig()
            ).then( response => {
                if(response.data == "MODERATOR") {
                    this.setState({
                        hideCreate: false
                    });
                } else {
                    this.setState({
                        hideCreate: true
                    });
                }
            });
        } else {
            axios.get(
                getPathApiUserEvents(this.props.user),
                getConfig()
            ).then( response => {
                this.setState({
                    events: mapEvents(response.data)
                });
            });
        }

        axios.get(
            PATH_API_BIRTHDAY,
            getConfig()
        ).then(response => {
            this.setState({
                birthdays: mapEvents(response.data)
            });
        });

        jQuery('#calendar').fullCalendar({
            locale: 'ru',
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,agendaWeek,agendaDay'
            },
            selectable: true,
            selectHelper: true,
            showNonCurrentDates: false,
            startEditable: false,
            durationEditable: false,
            firstDay: 1,
            events: this.state.events,
            eventLimit: true,
            eventClick: function (calEvent, jsEvent, view) {
                if(calEvent.id == 0){
                    swal(calEvent.title);
                }
                else {
                    browserHistory.push('/event/' + calEvent.id);
                }
            },

        });
    }
}

export default Scheduler;
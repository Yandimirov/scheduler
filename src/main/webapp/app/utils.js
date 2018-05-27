import {PATH_API_USER, IMAGE_PATH, PATH_API_USERS, PATH_API_EVENT, PATH_API_EVENT_BY_TYPE} from './paths.js';
import axios from 'axios';
import moment from 'moment';

export function getPathApiUserMessages() {
    return PATH_API_USER + localStorage.getItem('userId') + '/messages/';
}
export function getPathApiUserChats() {
    return PATH_API_USER + localStorage.getItem('userId') + '/chats/';
}

export function getPathApiUserChatsAfter() {
    return PATH_API_USER + 'chats/';
}

export function getPathApiUserRole() {
    return PATH_API_USERS + '/' + localStorage.getItem('userId') + '/role';
}

export function getEventByType(type) {
    return PATH_API_EVENT + '/type/' + type;
}

export function getPathApiUserEvent(id) {
    return PATH_API_USER + 'event' + '/' + id;
}

export function getPathApiUserEvents(id){
    return PATH_API_USER + id + '/event';
}

export function getPathApiEvent(id) {
    return PATH_API_EVENT + '/' + id;
}

export function getPathApiEventUsers(id) {
    return PATH_API_EVENT + '/' + id + '/users';
}

export function getConfig() {
    let config = {
        headers: {"x-auth-token": localStorage.getItem('token')},
    };

    return config;
}

export function getImagePath(imageName) {
    return IMAGE_PATH + imageName;
}

export function getUser(item, index){
    return {
        name: item.firstName + ' ' + item.lastName,
        id: item.id,
        login: item.username
    }
}

export function getUsers() {
    let config = getConfig();
    let users = [];
}

export function mapEvents(srcEvents) {
    let events = [];
    for(let i = 0; i < srcEvents.length; i++){
        let srcEvent = srcEvents[i];
        let event = {
            id: srcEvent.id,
            title: srcEvent.info.name,
            start: moment(srcEvent.startDate).format(),
            end: moment(srcEvent.endDate).format()
        };
        events.push(event);
    }
    //console.log(events);
    return events;
}

export const GOOGLE_API_KEY = 'AIzaSyDvfQ9SFtGF0cwJwVxlusHZZ1GjHxnYs2A'
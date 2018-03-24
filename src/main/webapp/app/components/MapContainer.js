import React from 'react';
import {ExtendedGoogleMap} from './ExtendedGoogleMap';
import {getConfig, getPathApiEvent, getPathApiUserEvents} from '../utils.js';
import {PATH_API_EVENT} from '../paths.js';
import axios from 'axios';

export default class MapContainer extends React.Component {
    constructor(props){
        super(props);
        if(this.props.event == null){
            this.state = {
                markers: [{
                    id: '',
                    info: {
                        name: '',
                        place: {
                            id: '',
                            lat: '',
                            lon: '',
                        }
                    },
                    showinfo: false
                }],
                center: {
                    lat: 59.9342802,
                    lng: 30.3350986
                }
            };
        } else {
            this.state = {
                markers: [{
                    id: '',
                    info: {
                        name: '',
                        place: {
                            id: '',
                            lat: '',
                            lon: '',
                        }
                    },
                    showinfo: false
                }],
                center: this.props.center
            };
        }
        this.handleMapLoad = this.handleMapLoad.bind(this);
        this.handleMapClick = this.handleMapClick.bind(this);
        this.handleMarkerRightClick = this.handleMarkerRightClick.bind(this);
        this.handleMarkerClick = this.handleMarkerClick.bind(this);
        this.handleCloseClick = this.handleCloseClick.bind(this);

    }

    componentDidMount(){
        if(typeof(this.props.user) === 'undefined'){
            if(this.props.event != null){
                axios.get(
                    getPathApiEvent(this.props.event),
                    getConfig()
                ).then(response => {
                    let srcEvent = response.data;
                    let event = {
                        id : srcEvent.id,
                        info: srcEvent.info,
                        showinfo: false
                    };
                    this.setState({
                        markers: [event],
                        center:{
                            lat: event.info.place.lat,
                            lng: event.info.place.lon
                        }
                    });
                });
            } else {
                axios.get(
                    PATH_API_EVENT,
                    getConfig()
                ).then(response => {
                    let events = [];
                    let srcEvents = response.data;
                    for(let i = 0 ; i < srcEvents.length; i++){
                        if(srcEvents[i].info.place.id != 0){
                            srcEvents[i].info.place.lat += Math.random() * 0.00001;
                            srcEvents[i].info.place.lon += Math.random() * 0.00001;
                            events.push({
                                id : srcEvents[i].id,
                                info: srcEvents[i].info,
                                showinfo: false
                            });
                        }
                    }
                    this.setState({
                        markers: events,
                    })
                });
            }
        } else {
            axios.get(
                getPathApiUserEvents(this.props.user),
                getConfig()
            ).then( response =>{
                let events = [];
                let srcEvents = response.data;
                for(let i = 0 ; i < srcEvents.length; i++){
                    if(srcEvents[i].info.place.id != 0){
                        srcEvents[i].info.place.lat += Math.random() * 0.00001;
                        srcEvents[i].info.place.lon += Math.random() * 0.00001;
                        events.push({
                            id : srcEvents[i].id,
                            info: srcEvents[i].info,
                            showinfo: false
                        });
                    }
                }
                this.setState({
                    markers: events,
                })
            });
        }
    };

    handleMapLoad(map) {
        this._mapComponent = map;
        if (map) {
            map.getZoom();
        }
    }

    /*
     * This is called when you click on the map.
     * Go and try click now.
     */
    handleMapClick(event) {
        // const nextMarkers = [
        //     ...this.state.markers,
        //     {
        //         position: event.latLng,
        //         defaultAnimation: 2,
        //         key: Date.now(), // Add a key property for: http://fb.me/react-warning-keys
        //     },
        // ];
        // this.setState({
        //     markers: nextMarkers,
        // });
        //
        // if (nextMarkers.length === 3) {
        //     this.props.toast(
        //         `Right click on the marker to remove it`,
        //         `Also check the code!`
        //     );
        // }
    }

    handleMarkerRightClick(targetMarker) {
        /*
         * All you modify is data, and the view is driven by data.
         * This is so called data-driven-development. (And yes, it's now in
         * web front end and even with google maps API.)
         */
        // const nextMarkers = this.state.markers.filter(marker => marker !== targetMarker);
        // this.setState({
        //     markers: nextMarkers,
        // });
    }


    handleMarkerClick(targetMarker) {
        this.setState({
            markers: this.state.markers.map(marker => {
                if (marker === targetMarker) {
                    return {
                        ...marker,
                        showInfo: true,
                    };
                }
                return marker;
            }),
        });
    }

    handleCloseClick(targetMarker) {
        this.setState({
            markers: this.state.markers.map(marker => {
                if (marker === targetMarker) {
                    return {
                        ...marker,
                        showInfo: false,
                    };
                }
                return marker;
            }),
        });
    }

    render() {
        return (
            <div style={{height: this.props.height}}>
                <ExtendedGoogleMap
                    containerElement={
                        <div style={{width: this.props.width, height: this.props.height}} />
                    }
                    mapElement={
                        <div style={{ height: this.props.height}} />
                    }
                    onMapLoad={this.handleMapLoad}
                    onMapClick={this.handleMapClick}
                    onMarkerClick={this.handleMarkerClick}
                    onCloseClick={this.handleCloseClick}
                    defaultCenter={this.state.center}
                    markers={this.state.markers}
                    onMarkerRightClick={this.handleMarkerRightClick}
                />
            </div>
        );
    }
}
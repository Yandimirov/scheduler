import React from 'react';
import { withGoogleMap, GoogleMap, Marker, InfoWindow} from "react-google-maps";
import {Link} from 'react-router';

export const ExtendedGoogleMap = withGoogleMap(props => (
    <GoogleMap
        ref={props.onMapLoad}
        defaultZoom={8}
        defaultCenter={props.defaultCenter}
        onClick={props.onMapClick}
    >
                {props.markers.map((marker, index) => {
                    const onClick = () => props.onMarkerClick(marker);
                    const onCloseClick = () => props.onCloseClick(marker);

                    return (

                            <Marker
                                key={marker.id}
                                position={{
                                    lat: marker.info.place.lat,
                                    lng: marker.info.place.lon,
                                }}
                                title={marker.info.name}
                                onClick={onClick}
                            >
                                {marker.showInfo && (
                                    <InfoWindow onCloseClick={onCloseClick}>
                                        <div>
                                            <Link to={"/event/" + marker.id}><strong>{marker.info.name}</strong></Link>
                                            <br />
                                            Описание: <em>{marker.info.description}</em>
                                        </div>
                                    </InfoWindow>
                                )}
                            </Marker>

                    );
            })}
    </GoogleMap>
));
import React from 'react';
import Scheduler from './Scheduler.js';
import MapContainer from './MapContainer.js';
import {Tab, Tabs} from 'material-ui/Tabs';

const styles = {
    headline: {
        fontSize: 24,
        marginBottom: 12,
        fontWeight: 400,
    },
};


class SchedulerComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: 'a',
            events: []
        };
        this.handleChange = this.handleChange.bind(this);
    };

    componentDidMount(){

    };

    handleChange(event) {
        this.setState({
            value: event,
        });
        //this.props.shouldUpdate();
    };

    render() {
        return (
            <Tabs
                value={this.state.value}
                onChange={this.handleChange}
            >
                <Tab label="Календарь" value="a">
                    <div><Scheduler ref="scheduler"/></div>
                </Tab>
                <Tab label="Карта" value="b">
                    <MapContainer width={1470} height={900} event={null}/>
                </Tab>
            </Tabs>
        );
    }
}

export default SchedulerComponent;

import * as React from "react";
import {DropDownMenu, FlatButton, MenuItem} from "material-ui";
import {PATH_REPORTS_EVENT} from "../paths";

export default class EventHistory extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            value: 1,
            type: "PDF",
            eventId: props.eventId
        };
    }

    handleChange = (event, index, value) => {
        let type = (value === 2) ? "XLS" : "PDF";

        this.setState({
            value: value,
            type: type,
        });
    };

    render() {
        return (
            <div>
                <DropDownMenu value={this.state.value} onChange={this.handleChange}>
                    <MenuItem value={1} primaryText="PDF" />
                    <MenuItem value={2} primaryText="XLS" />
                </DropDownMenu>
                <FlatButton label="Скачать отчет"
                            primary={true}
                            href={PATH_REPORTS_EVENT + this.state.eventId +"?type=" + this.state.type}
                />
            </div>
        );
    }
}
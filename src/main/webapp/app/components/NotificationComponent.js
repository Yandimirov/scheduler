/**
 * Created by Mikhail on 21.04.2017.
 */
import React from 'react';
import Dialog from 'material-ui/Dialog';

export default class NotificationComponent extends React.Component{
    constructor(props){
        super(props);
        this.state= {
            notification1: {
                date: '',
                time: ''
            },
            notification2: {
                date: '',
                time: ''
            },
            notification3: {
                date: '',
                time: ''
            },
            open: this.props.open,

        }
    };

    handleOpen = () => {
        this.setState({open: true});
    };

    handleClose = () => {
        this.setState({open: false});
    };

    render(){
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
                onTouchTap={this.handleClose}
            />,
        ];

        return(
            <div className="event-notifications">
                <Dialog
                    title="Напоминания"
                    actions={actions}
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
                                     onChange = {this.handleStartDateChange}
                        />
                        <TimePicker value = {this.state.notification1.time}
                                    hintText="Время"
                                    okLabel="Ок"
                                    cancelLabel="Отмена"
                                    format="24hr"
                                    style = {{marginLeft: '24px'}}
                                    onChange = {this.handleStartTimeChange}
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
                                     onChange = {this.handleStartDateChange}
                        />
                        <TimePicker value = {this.state.notification2.time}
                                    hintText="Время"
                                    okLabel="Ок"
                                    cancelLabel="Отмена"
                                    format="24hr"
                                    style = {{marginLeft: '24px'}}
                                    onChange = {this.handleStartTimeChange}
                        />
                    </div>
                </Dialog>
            </div>
        );
    }
}
import React from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import {Link} from 'react-router';

import PersonalEditor from './PersonalEditor.js';

const UserEditor = React.createClass({

    handleUpdate: function (e) {
        this.props.shouldUpdate(e);
    },


    render: function () {
        return (
            <div className="editor">
                <div className="label"><b>Редактирование информации</b></div>
                <div className="inline">
                    <PersonalEditor
                        firstName={this.props.firstName}
                        lastName={this.props.lastName}
                        birthday={this.props.birthday}
                        city={this.props.city}
                        email={this.props.email}
                        imagePath={this.props.imagePath}
                        infoUpdate={this.handleUpdate}
                    />
                </div>
                <div>
                    <Link to="/">
                        <RaisedButton label="Назад" primary={true}/>
                    </Link>
                </div>

            </div>
        );
    }
});

export default UserEditor;
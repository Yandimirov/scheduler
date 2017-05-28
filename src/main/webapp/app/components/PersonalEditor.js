import React from 'react';
import TextField from 'material-ui/TextField';
import FontIcon from 'material-ui/FontIcon';
import RaisedButton from 'material-ui/RaisedButton';
import DatePicker from 'material-ui/DatePicker';
import axios from 'axios';
import swal from 'sweetalert';
import 'swal-forms';
import {getConfig} from '../utils.js';
import {IMAGE_PATH, PATH_API_USER} from "../paths.js";

const regEmail = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;

const styles = {
    button: {
        margin: 12,
    },
    imageInput: {
        cursor: 'pointer',
        position: 'absolute',
        top: 0,
        bottom: 0,
        right: 0,
        left: 0,
        width: '100%',
        opacity: 0,
    },
    hintStyle: {
        color: '#000000',
    },
};


const PersonalEditor = React.createClass({
    getInitialState(){
        return {
            formData: null,
            firstName: "",
            lastName: "",
            birthday: "",
            city: "",
            imagePath: "",
            email: ""
        }
    },
    componentDidMount(){
        this.setState({
            firstName: this.props.firstName,
            lastName: this.props.lastName,
            birthday: this.props.birthday,
            city: this.props.city,
            email: this.props.email
        });
    },

    handleSubmit(e){
        e.preventDefault();
        if (this.state.formData != null) {
            axios.post(IMAGE_PATH, this.state.formData, getConfig())
                .then(response => {
                    this.props.infoUpdate("true");
                });
        }
        //setTimeout(, 1000);
    },

    handleFile(e){
        e.preventDefault();
        let fd = new FormData();
        fd.append('file', e.target.files[0]);
        fd.append('token', localStorage.getItem('token'));
        this.state.formData = fd;
    },

    handleFirstNameChange(event){
        this.setState(
            {
                firstName: event.target.value
            }
        );
    },

    handleLastNameChange(event){
        this.setState(
            {
                lastName: event.target.value
            }
        );
    },

    handleCityChange(event){
        this.setState(
            {
                city: event.target.value
            }
        );
    },

    handleEmailChange(event){
        if (regEmail.test(e.target.value)) {
            this.setState(
                {
                    email: event.target.value
                }
            );
        }
    },

    handleBirthdayChange(event, date){
        this.setState(
            {
                birthday: date
            }
        );
    },

    handleUpdateClick(){
        let update = this.props.infoUpdate;
        swal.withForm({
            title: 'Введите пароль',
            text: 'Введите ваш пароль',
            showCancelButton: true,
            confirmButtonColor: '#11bed1',
            confirmButtonText: 'Обновить',
            cancelButtonText: 'Отмена',
            closeOnConfirm: true,
            formFields: [
                { id: 'password', placeholder:'Пароль', type: 'password'},
            ]
        }, function(isConfirm) {
            if(isConfirm){
                let authDto = {
                    password: this.swalForm.password,
                };
                axios.put(
                    PATH_API_USER,
                    authDto,
                    getConfig()
                ).then(response => {
                    update("true");
                }).catch(error => {
                    swal("Невернно введен пароль!");
                });
            }
        })
    },

    render: function () {
        const {
            firstName,
            lastName,
            birthday,
            city,
            email,
            imagePath
        } = this.props;
        return (
            <div className="personal-editor">
                <div>Основное</div>
                <div>
                    <div className="inline">
                        <div className="image-div"><img className="user-photo" src={IMAGE_PATH + imagePath}
                                                        onLoad={this.changeSize}/></div>
                        <form encType="multipart/form-data" onSubmit={this.handleSubmit}>
                            <div>Максимальный размер фотографии - 512кб</div>
                            <RaisedButton
                                label="Выбрать фото"
                                labelPosition="before"
                                style={styles.button}
                                containerElement="label"
                            ><input type="file" onChange={this.handleFile} style={styles.imageInput} accept="image/*"/>
                            </RaisedButton>
                            <RaisedButton
                                label="Сохранить"
                                labelPosition="before"
                                style={styles.button}
                                containerElement="label"
                            ><input type="submit" style={styles.imageInput}/>
                            </RaisedButton>
                        </form>
                    </div>

                    <div className="inline">
                        <TextField hintText={firstName} name="firstName"
                                   hintStyle={styles.hintStyle}
                                   onChange={this.handleFirstNameChange}
                                   value={this.state.firstName}
                                   style={{marginLeft: '24px'}}
                                   inputStyle={{color: '#000000'}}
                                   disabled={true}/><br />
                        <TextField hintText={lastName} name="lastName"
                                   hintStyle={styles.hintStyle}
                                   onChange={this.handleLastNameChange}
                                   value={this.state.lastName}
                                   style={{marginLeft: '24px', color: '#000000'}}
                                   inputStyle={{color: '#000000'}}
                                   disabled={true}/><br />
                        <FontIcon className="material-icons">cake</FontIcon> <DatePicker hintText={birthday}
                                                                                         value={this.state.birthday}
                                                                                         container="inline"
                                                                                         style={{display: 'inline-block', color: '#000000'}}
                                                                                         hintStyle={styles.hintStyle}
                                                                                         name="date"
                                                                                         onChange={this.handleBirthdayChange}
                                                                                         disabled={true}/> <br/>
                        <FontIcon className="material-icons">location_city</FontIcon> <TextField hintText={city}
                                                                                                 value={this.state.city}
                                                                                                 name="city"
                                                                                                 inputStyle={{color: '#000000'}}
                                                                                                 hintStyle={styles.hintStyle}
                                                                                                 onChange={this.handleCityChange}
                                                                                                 disabled={true}/>
                        <br/>
                        <FontIcon className="material-icons">email</FontIcon> <TextField hintText={email}
                                                                                         value={this.state.email}
                                                                                         name="email"
                                                                                         inputStyle={{color: '#000000'}}
                                                                                         hintStyle={styles.hintStyle}
                                                                                         onChange={this.handleEmailChange}
                                                                                         disabled={true}/>
                        <br/>
                        <RaisedButton
                            label="Обновить информацию"
                            onTouchTap={this.handleUpdateClick}
                        />
                    </div>
                </div>

            </div>
        );
    }
});

export default PersonalEditor;
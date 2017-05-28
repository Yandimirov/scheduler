const SERVER_HOST = "http://localhost:8080";

function parseDate(date) {
    if(date === ""){
        return "";
    } else {
        let day = date.getDate();
        let month = date.getMonth() + 1;
        let year = date.getFullYear();
        return "" + day + "." + month + "." + year;
    }
}

const UserInfoComponent = React.createClass({
   render(){
       const {
           firstName,
           lastName,
           birthday,
           city,
           email
       } = this.props;

       return(
           <div>
               <div>
                   <p>firstName: {firstName}</p>
                   <p>lastName: {lastName}</p>
                   <p>birthday: {birthday}</p>
                   <p>city: {city}</p>
                   <p>email: {email}</p>
               </div>
           </div>
       );
   }
});

const SkillComponent = React.createClass({
   render(){
       const {
           name,
           description
       } = this.props;

       return(
           <div>
               <p>skill: {name}</p>
               <p>level: {description}</p>
           </div>
       );
   }
});

let skillId = 0;

const UserComponent = React.createClass({


    getInitialState() {
       return{
           userInfo: {
               firstName: "",
               lastName: "",
               birthday: "",
               city: "",
               email: "",
               skills: [],
               wish: []
           }
       }
   },

   componentDidMount(){
       axios.get(SERVER_HOST + "/api/user/0").then(response => {
           this.setState({
               userInfo: response.data
           });
       });

       axios.post(SERVER_HOST + "/api/test");
   },

   render(){
       const userInfo = this.state.userInfo;

       const user = <UserInfoComponent
           firstName={userInfo.firstName}
           lastName={userInfo.lastName}
           birthday={parseDate(new Date(userInfo.birthday))}
           city={userInfo.city}
           email={userInfo.email}
       />;

       const skills = this.state.userInfo.skills.map(skill =>
            <SkillComponent
                key={skillId++}
                name={skill.name}
                description={skill.description}
            />
       );

       return (
           <div>
               {user}
               {skills}
           </div>
       );
   }
});

//UI to add skill
const AddSkillBtn = React.createClass({
    render(){
        return(
            <button
                onClick={this.props.onClick}
            >
                {this.props.btntxt}
            </button>
        )
    }
})

const AddSkillFormComponent = React.createClass({
    getInitialState() {
        return {
            nameValue: '',
            descriptionValue: ''
        }
    },

    handleChange(event) {
        if (event.target.name == "skill")
            this.setState({nameValue: event.target.value});
        if (event.target.name == "description")
            this.setState({descriptionValue: event.target.value});
    },
    render(){
        return(
            <form style={{display: this.props.display }}  onChange={this.handleChange}>
                Name:
                <input name="skill" type="text" value={this.props.value}  />
                <input name="description" type="text" value={this.props.value}  />
                <input type="button" value="Add" onClick={this.props.onClickAddUser}/>
            </form>
        )
    }
})

const AddSkillComponent = React.createClass({

    getInitialState(){
        return{
            formDisplay: 'none',
            addNewSkillBtnTxt: 'Add new skill',
        };
    },



    handleClickShowForm(e){
        console.log("pressed");
        this.setState({formDisplay: (this.state.formDisplay == 'none') ? 'inline' : 'none'});
        this.setState({addNewSkillBtnTxt: (this.state.addNewSkillBtnTxt == 'Add new skill') ? 'Hide' : 'Add new skill'});
    },

    handleOnClickAddUser(event){
        axios.post(SERVER_HOST + "/api/addskill",
            {
                name: event.target.parentNode.skill.value,
                description: event.target.parentNode.description.value
            });
        
    },

    render(){

        var formDisplay = this.state.formDisplay;

        return (
            <div>
                <AddSkillFormComponent
                    display={formDisplay}
                    onChange={this.handleOnChangeForm}
                    onClickAddUser={this.handleOnClickAddUser}
                />
                < AddSkillBtn
                btntxt = {this.state.addNewSkillBtnTxt}
                onClick = {this.handleClickShowForm}
                />
            </div>
        )
    }
});

const App = React.createClass({
   render(){
       return(
           <div>
                <UserComponent/>
                <AddSkillComponent/>
           </div>
       );
   }
});

ReactDOM.render(
    <App />,
    document.getElementById("react")
);
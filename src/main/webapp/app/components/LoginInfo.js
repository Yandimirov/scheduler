import React from 'react';

const LoginInfo = React.createClass({
    render: function () {
        return (
            <div className="login-info">
                <div>
                    <b>Добро пожаловать!</b><br/>
                    Scheduler - планировщик корпоративных событий.
                </div>

                Данное приложение - ваш помощник в планировании событий<br/>
                Чтобы начать пользоваться приложением, вы должны войти под своей корпоративной учетной записью<br/>
            </div>
        );
    }
});

export default LoginInfo ;

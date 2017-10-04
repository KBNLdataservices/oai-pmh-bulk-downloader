import React from "react";
import Header from "./layout/header";
import { Link } from "react-router";
import { urls } from "../etc/urls";

class App extends React.Component {

    render() {
        return (
            <div>
                <Header username={this.props.username} socketClosed={this.props.socketClosed} />
                <div className="container">
                  <ul className="list-inline">
                    <li><Link to="/" >Dashboard</Link></li>
                    <li><Link to={urls.stylesheets()} activeStyle={{color: "#888"}}>Stylesheet beheer</Link></li>
                  </ul>
                    {this.props.children}
                </div>
            </div>
        )
    }
}

export default App;

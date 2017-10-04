import React from "react";

class Stylesheets extends React.Component {

    constructor(props) {
      super(props);

      this.state = {
        currentXslt: null
      };

    }

    onFile(ev, name = null) {
      if (name) {
        this.props.onStylesheetUpdate(ev.target.files, name);
      } else {
        this.props.onUploadNewStylesheet(ev.target.files);
      }
      ev.target.value = null;
    }

    render() {
      const { stylesheets } = this.props;

      const grouped = stylesheets.reduce((acc, curr) => {
        acc[curr.name] = acc[curr.name] || [];
        acc[curr.name].push(curr);
        return acc;
      }, {});

      return (
        <div>
          <ul className="list-group">
            {Object.keys(grouped).map((key) => {
              const { id, created, name, xslt } = grouped[key].find(s => s.isLatest);
              return (
                <li key={key} className="list-group-item">
                  <span className="col-md-1">{id}</span>
                  <span className="col-md-4">{name}</span>
                  <span className="col-md-10 text-right">
                    Laatste versie: {" "}
                    {created[2]}-{created[1]}-{created[0]}{" "}
                    ({created[3]}:{created[4]}:{created[5]})
                  </span>
                  <span className="col-md-3 text-right">versies: {grouped[key].length}</span>
                  <span className="col-md-12 text-right">
                    <input type="file" className="pull-right" onChange={(ev) => this.onFile(ev, name)}  />
                    <span style={{marginRight: "6px"}}>Nieuwe versie uploaden:</span>
                  </span>
                  <span className="col-md-2 text-right">
                    <a style={{cursor: "pointer"}} onClick={() => {
                        if (this.state.currentXslt === name) {
                          this.setState({currentXslt: null});
                        } else {
                          this.setState({currentXslt: name});
                        }
                      }}>
                      Tonen{" "}
                      <span className={`glyphicon ${this.state.currentXslt === name ?
                         "glyphicon-triangle-bottom" : "glyphicon-triangle-right"}`} />
                    </a>
                  </span>
                  <div className="clearfix" />
                  <pre style={{display: this.state.currentXslt === name ? "block" : "none"}}>
                    {xslt}
                  </pre>
                </li>
              );
            })}
          </ul>
          Nieuwe stylesheet uploaden: <input type="file" onChange={(ev) => this.onFile(ev)}  />

        </div>
      );
    }
}

export default Stylesheets;

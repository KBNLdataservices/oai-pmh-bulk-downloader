import xhr from "xhr";
import ActionTypes from "./action-types";
import {handleResponse} from "./response-handler";

const fetchStylesheets = (next = () => {}) => (dispatch) => {
    xhr({url: `/stylesheets?${new Date().getTime()}`, method: "GET", headers: {'Authorization': localStorage.getItem("authToken")}},
        (err, resp, body) => handleResponse(resp, () => {
            dispatch({type: ActionTypes.RECEIVE_STYLESHEETS, data: JSON.parse(body)});
            next();
        })
    );
};

const uploadStylesheet = (files, stylesheetName = null) => (dispatch) => {
  const data = new FormData();
  data.append("file", files[0]);

  const req = new XMLHttpRequest();

  req.onload = () => {
      if (req.status > 299) {
        alert(`Fout bij upload: ${JSON.parse(req.response).message}`);
      }
  }

  if (stylesheetName) {
    req.open("PUT", `/stylesheets/${stylesheetName}`);
  } else {
    req.open("POST", `/stylesheets`);
  }
  req.send(data)
}

export { fetchStylesheets, uploadStylesheet }

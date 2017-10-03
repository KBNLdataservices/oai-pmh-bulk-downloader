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

export { fetchStylesheets }

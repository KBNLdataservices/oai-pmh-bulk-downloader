import xhr from "xhr";
import {handleResponse} from "./response-handler";
import ActionTypes from "./action-types";


const fetchCredentials = () => (dispatch) => {
  dispatch({type: ActionTypes.RECEIVE_CREDENTIALS, data: {}});
};

export {fetchCredentials};

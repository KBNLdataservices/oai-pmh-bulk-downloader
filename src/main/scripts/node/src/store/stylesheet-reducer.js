import ActionTypes from "../actions/action-types";

const initialState = {
    list: [],
};


export default function(state=initialState, action) {
    switch (action.type) {
        case ActionTypes.RECEIVE_STYLESHEETS:
            return {
                ...state,
                list: (action.data || []),
            };
        default:
    }

    return state;
}

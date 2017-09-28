import {connectSocket} from "./actions/socket-listener";
import {fetchInitialData} from "./actions/fetch-initial-data";

// Renders the app when token is present
const authenticateAndInitialize = (onInitialize) => {
    localStorage.setItem("authToken", "dummy-token");
    // Fetches initial render data via xhr and will invoke onInitialize callback when received
    fetchInitialData(onInitialize);
    // Connects to websocket, which receives updates on render data
    connectSocket();
};

export { authenticateAndInitialize };

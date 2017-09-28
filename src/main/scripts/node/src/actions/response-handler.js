const handleResponse = (resp, next = () => {}) => {
    if (resp.statusCode > 400) {
      console.log(resp);
    } else {
        next();
    }
};

export { handleResponse }

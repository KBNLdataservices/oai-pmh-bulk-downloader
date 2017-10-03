export default (state, routed) => ({

    stylesheets: state.stylesheets.list,
    underEdit: typeof routed.params.repositoryId  === 'undefined'
    ? state.repositories.underEdit || {
            name: "",
            url: "",
            set: "",
            metadataPrefix: "",
            dateStamp: null,
            stylesheetId: undefined,
            schedule: "DAILY"
    }
    : state.repositories.underEdit || {
            ...state.repositories.list.find((repo) => repo.id === parseInt(routed.params.repositoryId, 10))
    },
    validationResultsUnderEdit: state.repositories.validationResultsUnderEdit || {}
});

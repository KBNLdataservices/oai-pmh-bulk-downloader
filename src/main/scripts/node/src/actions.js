import {fetchInitialData} from "./actions/fetch-initial-data";
import {
    enableRepository,
    disableRepository,
    validateNewRepository,
    saveRepository,
    deleteRepository
} from "./actions/repositories";

import { startHarvest, interruptHarvest } from "./actions/harvesters";
import { disableOaiRecordFetcher, startOaiRecordFetcher } from "./actions/oai-record-fetcher";
import { fetchRecord, findRecords } from "./actions/records";

import ActionTypes from "./action-types";

export default function actionsMaker(navigateTo, dispatch) {
    return {
        onEnableRepository: (id) => dispatch(enableRepository(id)),
        onDisableRepository: (id) => dispatch(disableRepository(id)),
        onValidateNewRepository: (repository) => dispatch(validateNewRepository(repository)),
        onStartHarvest: (repositoryId) => dispatch(startHarvest(repositoryId)),
        onInterruptHarvest: (repositoryId) => dispatch(interruptHarvest(repositoryId)),
        onSaveRepository: () => dispatch(saveRepository(() => navigateTo("root"))),
        onDeleteRepository: (id) => dispatch(deleteRepository(id, () => navigateTo("root", []))),
        onRefetchRepositories: () => fetchInitialData(() => {}),

        onStartOaiRecordFetcher: () => dispatch(startOaiRecordFetcher()),
        onDisableOaiRecordFetcher: () => dispatch(disableOaiRecordFetcher()),

        onFindRecords: (query) => dispatch(findRecords(query)),
        onClearFoundRecords: () => dispatch({type: ActionTypes.CLEAR_FOUND_RECORDS}),
        onNavigateTo: (key, params) => navigateTo(key, params),

        onFetchRecord: (kbObjId) => dispatch(fetchRecord(kbObjId))
    };
}
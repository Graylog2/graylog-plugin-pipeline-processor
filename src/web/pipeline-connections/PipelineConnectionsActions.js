import Reflux from 'reflux';

const PipelineConnectionsActions = Reflux.createActions({
  list: { asyncResult: true },
  connectToStream: { asyncResult: true },
});

export default PipelineConnectionsActions;

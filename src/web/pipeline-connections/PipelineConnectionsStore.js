import Reflux from 'reflux';

import UserNotification from 'util/UserNotification';
import URLUtils from 'util/URLUtils';
import fetch from 'logic/rest/FetchProvider';

import PipelineConnectionsActions from './PipelineConnectionsActions';

const urlPrefix = '/plugins/org.graylog.plugins.pipelineprocessor';

const PipelineConnectionsStore = Reflux.createStore({
  listenables: [PipelineConnectionsActions],
  connections: undefined,

  getInitialState() {
    return {connections: this.connections};
  },

  list() {
    const failCallback = (error) => {
      UserNotification.error('Fetching pipeline connections failed with status: ' + error.message,
        'Could not retrieve pipeline connections');
    };

    const url = URLUtils.qualifyUrl(urlPrefix + '/system/pipelines/connections');
    const promise = fetch('GET', url);
    promise.then((response) => {
      this.connections = response;
      this.trigger({ connections: response });
    }, failCallback);
  },

  connectToStream(connection) {
    const url = URLUtils.qualifyUrl(urlPrefix + '/system/pipelines/connections/to_stream');
    const updatedConnection = {
      stream_id: connection.stream,
      pipeline_ids: connection.pipelines,
    };
    const promise = fetch('POST', url, updatedConnection);
    promise.then(
      response => {
        if (this.connections.filter(c => c.stream_id === response.stream_id)[0]) {
          this.connections = this.connections.map(c => (c.stream_id === response.stream_id ? response : c));
        } else {
          this.connections.push(response);
        }

        this.trigger({connections: this.connections});
        UserNotification.success(`Pipeline connections updated successfully`);
      },
      this._failUpdateCallback);
  },

  connectToPipeline(reverseConnection) {
    const url = URLUtils.qualifyUrl(`${urlPrefix}/system/pipelines/connections/to_pipeline`);
    const updatedConnection = {
      pipeline_id: reverseConnection.pipeline,
      stream_ids: reverseConnection.streams,
    };
    const promise = fetch('POST', url, updatedConnection);
    promise.then(
      response => {
        response.forEach(connection => {
          if (this.connections.filter(c => c.stream_id === connection.stream_id)[0]) {
            this.connections = this.connections.map(c => (c.stream_id === connection.stream_id ? connection : c));
          } else {
            this.connections.push(connection);
          }
        });

        this.trigger({ connections: this.connections });
        UserNotification.success('Pipeline connections updated successfully');
      },
      this._failUpdateCallback);
  },

  _failUpdateCallback(error) {
    UserNotification.error(`Updating pipeline connections failed with status: ${error.message}`,
      'Could not update pipeline connections');
  },
});

export default PipelineConnectionsStore;

import React from 'react';
import { Col, Row } from 'react-bootstrap';

import { Spinner } from 'components/common';
import MessageShow from 'components/search/MessageShow';

const SimulationPreview = React.createClass({
  propTypes: {
    stream: React.PropTypes.object.isRequired,
    originalMessage: React.PropTypes.object,
    simulationResults: React.PropTypes.array,
    isLoading: React.PropTypes.bool,
    error: React.PropTypes.object,
  },

  componentDidMount() {
    this.style.use();
  },

  componentWillUnmount() {
    this.style.unuse();
  },

  style: require('!style/useable!css!./SimulationPreview.css'),

  render() {
    if (!this.props.originalMessage && !this.props.simulationResults) {
      return null;
    }

    const streams = {};
    streams[this.props.stream.id] = this.props.stream;

    let originalMessagePreview = (this.props.isLoading ? <Spinner /> : null);
    if (this.props.originalMessage) {
      originalMessagePreview = (
        <MessageShow message={this.props.originalMessage}
                     streams={streams}
                     disableTestAgainstStream
                     disableSurroundingSearch
                     disableFieldActions />
      );
    }

    let simulationPreview = (this.props.isLoading ? <Spinner /> : null);
    if (this.props.simulationResults) {
      simulationPreview = this.props.simulationResults.map(message => {
        return (
          <MessageShow key={message.id}
                       message={message}
                       streams={streams}
                       disableTestAgainstStream
                       disableSurroundingSearch
                       disableFieldActions
                       isSimulation />
        );
      });
    }

    return (
      <Row>
        <Col md={12}>
          <hr />
        </Col>
        <Col md={6}>
          <h1>Original message</h1>
          <p>This is the original message loaded from Graylog.</p>
          <div className="message-preview-wrapper">
            {originalMessagePreview}
          </div>
        </Col>
        <Col md={6}>
          <h1>Simulation results</h1>
          <p>This is the result of processing the loaded message:</p>
          <div className="message-preview-wrapper">
            {simulationPreview}
          </div>
        </Col>
      </Row>
    );
  },
});

export default SimulationPreview;

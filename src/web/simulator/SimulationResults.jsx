import React from 'react';
import { Alert, Col, Row } from 'react-bootstrap';

import { Spinner } from 'components/common';
import MessageShow from 'components/search/MessageShow';

import SimulationPreview from './SimulationPreview';

import NumberUtils from 'util/NumberUtils';

const SimulationResults = React.createClass({
  propTypes: {
    stream: React.PropTypes.object.isRequired,
    originalMessage: React.PropTypes.object,
    simulationResults: React.PropTypes.object,
    isLoading: React.PropTypes.bool,
    error: React.PropTypes.object,
  },

  componentDidMount() {
    this.style.use();
  },

  componentWillUnmount() {
    this.style.unuse();
  },

  style: require('!style/useable!css!./SimulationResults.css'),

  _getViewComponent(streams) {
    if (this.props.isLoading) {
      return <Spinner/>;
    }

    return <SimulationPreview simulationResults={this.props.simulationResults} streams={streams} />;
  },

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

    let errorMessage;
    if (this.props.error) {
      errorMessage = (
        <Alert bsStyle="danger">
          <p><strong>Error simulating message processing</strong></p>
          <p>
            Could not simulate processing of message <em>{this.props.originalMessage.id}</em> in stream{' '}
            <em>{this.props.stream.title}</em>.
            <br />
            Please try loading the message again, or use another message for the simulation.
          </p>
        </Alert>
      );
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
          <p>
            {this.props.isLoading ?
              'Simulating message processing, please wait a moment.' :
              `These are the results of processing the loaded message. Processing took ${NumberUtils.formatNumber(this.props.simulationResults.took_microseconds)} µs.`}
          </p>
          {errorMessage}
          {this._getViewComponent(streams)}
        </Col>
      </Row>
    );
  },
});

export default SimulationResults;

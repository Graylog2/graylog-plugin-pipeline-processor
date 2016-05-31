import React from 'react';
import { Col, Row } from 'react-bootstrap';

import { Spinner } from 'components/common';
import LoaderTabs from 'components/messageloaders/LoaderTabs';
import MessageShow from 'components/search/MessageShow';

import SimulatorActions from './SimulatorActions';
import SimulatorStore from './SimulatorStore';

const ProcessorSimulator = React.createClass({
  propTypes: {
    stream: React.PropTypes.object.isRequired,
  },

  getInitialState() {
    return {
      message: undefined,
      simulation: undefined,
      loading: false,
      error: undefined,
    };
  },

  componentDidMount() {
    this.style.use();
  },

  componentWillUnmount() {
    this.style.unuse();
  },

  style: require('!style/useable!css!./ProcessorSimulator.css'),

  _onMessageLoad(message) {
    this.setState({ message: message, simulation: undefined, loading: true, error: undefined });

    SimulatorActions.simulate.triggerPromise(this.props.stream, message.index, message.id).then(
      messages => {
        this.setState({ simulation: messages, loading: false });
      },
      error => {
        this.setState({ loading: false, error: error });
      }
    );
  },

  render() {
    const streams = {};
    streams[this.props.stream.id] = this.props.stream;

    let originalMessagePreview;
    if (this.state.message) {
      originalMessagePreview = (
        <MessageShow message={this.state.message}
                     streams={streams}
                     disableTestAgainstStream
                     disableSurroundingSearch
                     disableFieldActions />
      );
    }

    let simulationPreview;
    if (this.state.simulation) {
      simulationPreview = (
        <MessageShow message={this.state.simulation[0]}
                     streams={streams}
                     disableTestAgainstStream
                     disableSurroundingSearch
                     disableFieldActions
                     isSimulation />
      );
    }

    let diff;
    if (this.state.loading) {
      diff = <Spinner />;
    } else if (this.state.message && this.state.simulation) {
      diff = (
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
            <p>{simulationPreview ? 'This is the result of processing the selected message:' : 'Select a message on the "Load a message" section to see a simulation.'}</p>
            <div className="message-preview-wrapper">
              {simulationPreview}
            </div>
          </Col>
        </Row>
      );
    }

    return (
      <div>
        <Row>
          <Col md={12}>
            <h1>Load a message</h1>
            <p>Load a message to be used in the simulation. <strong>No changes will be done in your stored
              messages.</strong></p>
            <LoaderTabs onMessageLoaded={this._onMessageLoad} disableMessagePreview />
          </Col>
        </Row>
        {diff}
      </div>
    );
  },
});

export default ProcessorSimulator;

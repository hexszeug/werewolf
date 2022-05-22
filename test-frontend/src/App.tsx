import React from "react";
import Game from "./game/Game";

type IProps = {}
type IState = {}

class App extends React.Component<IProps, IState> {
  // websocket?: WebSocket;

  constructor(props: IProps) {
    super(props);
  }

  componentWillUnmount() {
    // this.websocket?.close();
  }

  componentDidMount() {
    // this.websocket = new WebSocket("");
  }

  render() {
    return <Game />;
  }
}

export default App;
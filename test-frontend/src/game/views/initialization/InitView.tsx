import React from "react";
import { IPlayer } from "../../players/player/Player";

type IProps = {
  players: Array<IPlayer>;
}
type IState = {}

class InitView extends React.Component<IProps, IState> {
  constructor(props: IProps) {
    super(props);
  }

  render() {
    return null;
  }
}

export default InitView;
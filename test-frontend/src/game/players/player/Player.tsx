import React from "react";
import "./Player.scss";

interface IPlayer {
  playerID: string;
  status?: 
    "AWAKE" |
    "SLEEPING" |
    "DEAD";
  role?:
    "WEREWOLF" |
    "VILLAGER";
  tags: Array<{
    name: string;
    [key: string]: any;
  }>
};

type IProps = {
  player: IPlayer
}
type IState = {}

class Player extends React.Component<IProps, IState> {
  constructor(props: IProps) {
    super(props);
  }

  render() {
    const player = this.props.player;
    return (
      <div className="Player">
        <h1>{player.playerID.substring(0, 5)}</h1>
        <h2>Status: {player.status || "AWAKE"}</h2>
        <h2>Role: {player.role || "UNKNOWN"}</h2>
        <ul>
          {player.tags.map((tag) => {
            return <li key={tag.name}>{tag.name}</li>
          })}
        </ul>
      </div>
    );
  }
}

export default Player;
export type { IPlayer };
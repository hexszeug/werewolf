import React from "react";
import Player, { IPlayer } from "./players/player/Player";

type IPhase =
  "WEREWOLFVES" |
  "DEAD_REVEAL" |
  "ACCUSING" |
  "JUDGING" |
  "EXECUTION"

type IProps = {}
type IState = {}

class Game extends React.Component<IProps, IState> {
  constructor(props: IProps) {
    super(props);
  }

  render() {
    const players: Array<IPlayer> = [
      {
        playerID: "id a",
        role: "VILLAGER",
        tags: [],
      },
      {
        playerID: "id b",
        status: "SLEEPING",
        tags: [
          {
            name: "accused",
            accuser: "id c",
            test: "idrk",
          },
          {
            name: "raised hand",
          },
        ],
      },
      {
        playerID: "id c",
        role: "WEREWOLF",
        status: "AWAKE",
        tags: [],
      },
    ]
    return (
      <div>
        {players.map((player: IPlayer) => {
          return <Player player={player} />
        })}
      </div>
    );
  }
}

export default Game;
export type { IPhase };
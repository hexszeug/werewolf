import { IPlayer } from "./players/player/Player";

const players: {
  [key: string]: IPlayer,
} = {}

const handleMessage = (msg: [string, string, any]) => {
  switch (msg[1]) {
    case "player":
      let player = msg[2] as IPlayer;
      players[player.playerID] = Object.assign({}, players[player.playerID], player);
      break;
    case "phase":
      break;
  }
}

export { handleMessage };
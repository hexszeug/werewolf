import { handleMessage as handleGameMessage } from "../game/gamecontroller";

const handleMessage = (msg: [string, string, any]) => {
  switch (msg[0]) {
    case "game":
      handleGameMessage(msg);
      break;
  }
}

const sendMessage = (msg: [string, string, any]) => {
  console.log(msg);
}

export default sendMessage;
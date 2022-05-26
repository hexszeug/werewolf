import React from 'react';
import Game from './game/Game';
import { PlayerType } from './game/players/player/Player';

type MessageType = {
	type: string;
	data: unknown;
};
type MessageHandlerType = (message: MessageType) => void;

type PropsType = {};
type StateType = {
	phase: 'game' | 'session' | 'room';
};

class App extends React.Component<PropsType, StateType> {
	websocket!: WebSocket;
	playerID?: string;
	subscribedMessageHandlers: {
		[key: string]: MessageHandlerType;
	} = {};

	constructor(props: PropsType) {
		super(props);
		this.state = {
			phase: 'session',
		};
		this.subscribe = this.subscribe.bind(this);
		this.handleMessage = this.handleMessage.bind(this);
		this.send = this.send.bind(this);
	}

	componentDidMount() {
		this.websocket = new WebSocket('ws://localhost:80');
		this.websocket.onmessage = (e) => {
			this.handleMessage(e.data);
		};
		this.websocket.onopen =
			this.websocket.onclose =
			this.websocket.onerror =
				(e) => {
					console.warn(e);
				};
	}

	componentWillUnmount() {
		this.websocket.close();
	}

	render() {
		if (this.state.phase === 'game') {
			return (
				<div>
					<Game
						playerID={this.playerID}
						subscribe={this.subscribe}
						send={this.send}
					/>
					<p>{this.playerID}</p>
				</div>
			);
		}
		return (
			<div>
				<h1>Welcome to the test werewolf client</h1>
				<button
					onClick={() => {
						this.websocket.send('start');
					}}
				>
					Start game
				</button>
			</div>
		);
	}

	handleMessage(message: string) {
		const [path, type, rawData] = JSON.parse(message);
		const data = rawData as PlayerType;
		if (data.isMe) {
			this.playerID = data.playerID;
		}
		this.setState({ phase: path });
		if (path in this.subscribedMessageHandlers) {
			this.subscribedMessageHandlers[path]({ type, data });
		}
	}

	subscribe(path: string, handler: MessageHandlerType) {
		this.subscribedMessageHandlers[path] = handler;
	}

	send(path: string, type: string, data: unknown) {
		this.websocket.send(JSON.stringify([path, type, data]));
	}
}

export default App;
export type { MessageType, MessageHandlerType };

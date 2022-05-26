import React from 'react';
import { MessageHandlerType, MessageType } from '../App';
import { PlayerType } from './players/player/Player';
import { PlayerListType } from './players/Players';
import InitView from './views/initialization/InitView';
import WerewolfvesView from './views/werewolves/WerewolvesView';

type PhaseType =
	| 'GAME_START'
	| 'WEREWOLVES'
	| 'SUNRISE'
	| 'DEAD_REVEAL'
	| 'ACCUSING'
	| 'JUDGING'
	| 'EXECUTION'
	| 'SUNSET'
	| null;

type PropsType = {
	playerID?: string;
	subscribe: (path: string, recall: MessageHandlerType) => void;
	send: (path: string, type: string, data: unknown) => void;
};
type StateType = {
	players: PlayerListType;
	phase: PhaseType;
};

class Game extends React.Component<PropsType, StateType> {
	constructor(props: PropsType) {
		super(props);
		this.state = {
			players: {},
			phase: null,
		};
		this.handleMessage = this.handleMessage.bind(this);
	}

	componentDidMount() {
		this.props.subscribe('game', this.handleMessage);
	}

	render() {
		if (
			this.props.playerID &&
			this.state.players[this.props.playerID]?.status === 'SLEEPING'
		) {
			return <h1>Sleeping through this night.</h1>;
		}
		switch (this.state.phase) {
			case 'GAME_START':
				return <InitView players={this.state.players} />;
			case 'WEREWOLVES':
				return (
					<WerewolfvesView
						players={this.state.players}
						send={(type, data) => {
							this.props.send('game', type, data);
						}}
					></WerewolfvesView>
				);
			default:
				return (
					<div>
						<h1>No Phase started yet</h1>
						<p>Error</p>
					</div>
				);
		}
	}

	handleMessage(msg: MessageType) {
		switch (msg.type) {
			case 'player':
				const player = msg.data as PlayerType;
				console.log(player);
				this.setState((prevState) => ({
					players: {
						...prevState.players,
						[player.playerID]: {
							...prevState.players[player.playerID],
							...player,
						},
					},
				}));
				break;
			case 'phase':
				const phase = msg.data as PhaseType;
				this.setState({ phase: phase });
				console.log(phase);
				break;
		}
	}
}

export default Game;
export type { PhaseType };

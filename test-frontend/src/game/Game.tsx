import React from 'react';
import { MessageHandlerType, MessageType } from '../App';
import { PlayerType } from './players/player/Player';
import { PlayerListType } from './players/Players';
import { Statusbar } from './statusbar/Statusbar';
import { AccusingView } from './gameviews/Accusing';
import { DeadRevealView } from './gameviews/DeadReveal';
import { ExecutionView } from './gameviews/Execution';
import { GameStartView } from './gameviews/GameStart';
import { JudgingView } from './gameviews/Judging';
import { SunriseView } from './gameviews/Sunrise';
import { SunsetView } from './gameviews/Sunset';
import { WerewolfvesView } from './gameviews/Werewolves';
import Narrator from './narrator/Narrator';

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
		const players = this.state.players;
		const send = (type: string, data: unknown) => {
			this.props.send('game', type, data);
		};
		const gameView = (() => {
			if (
				this.props.playerID &&
				this.state.players[this.props.playerID]?.status === 'SLEEPING'
			) {
				return (
					<div>
						<h1>Sleeping through this night</h1>
						<p>You are currently sleeping</p>
					</div>
				);
			}
			switch (this.state.phase) {
				case 'GAME_START':
					return <GameStartView players={players} />;
				case 'WEREWOLVES':
					return (
						<WerewolfvesView
							players={players}
							send={send}
						/>
					);
				case 'SUNRISE':
					return <SunriseView players={players} />;
				case 'DEAD_REVEAL':
					return <DeadRevealView players={players} />;
				case 'ACCUSING':
					return <AccusingView players={players} />;
				case 'JUDGING':
					return <JudgingView players={players} />;
				case 'EXECUTION':
					return <ExecutionView players={players} />;
				case 'SUNSET':
					return <SunsetView players={players} />;
				default:
					return (
						<div>
							<h1>Illegal phase or something</h1>
							<p>Error</p>
						</div>
					);
			}
		})();
		return (
			<div>
				<Narrator phase={this.state.phase} />
				{gameView}
				{this.props.playerID && (
					<Statusbar player={players[this.props.playerID]} />
				)}
			</div>
		);
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

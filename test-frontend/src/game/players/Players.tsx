import './Players.scss';
import Player, { PlayerType, RenderPlayer } from './player/Player';
import { ObjectMap, ObjectValuesMap } from '../..';

export class PlayerList {
	[key: string]: RenderPlayer;

	constructor(players: { [key: string]: PlayerType }) {
		Object.assign(
			this,
			ObjectMap(players, (value) => [value[0], new RenderPlayer(value[1])]),
		);
	}

	get me() {
		const me = Object.values(this).find((value) => value.isMe);
		return me ? me : new RenderPlayer({ playerID: '', tags: [] });
	}
}

export const PlayerLists = {
	firstWithTag: (
		players: PlayerList,
		pattern: { name: string; [key: string]: any },
	) => {
		for (let player of Object.values(players)) {
			if (player.hasTag(pattern)) return player;
		}
		return null;
	},
};

type PropsType = {
	players: PlayerList;
	onClick?: (player: PlayerType) => void;
};

const Players = (props: PropsType) => {
	const players = ObjectValuesMap(props.players, (player) => {
		player = Object.assign({}, player);
		if (props.onClick) {
			player.onClick = props.onClick;
		}
		return (
			<Player
				key={player.playerID}
				player={player}
			/>
		);
	});
	return <div className='Players'>{players}</div>;
};

export default Players;

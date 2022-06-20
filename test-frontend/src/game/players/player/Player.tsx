import { ObjectValuesMap } from '../../..';
import './Player.scss';

type PlayerType = {
	playerID: string;
	nickname?: string;
	avatar?: string;
	isMe?: boolean;
	status?: 'AWAKE' | 'SLEEPING' | 'DEAD';
	role?: 'WEREWOLF' | 'VILLAGER';
	tags: { name: string; [key: string]: any }[];
};

class RenderPlayer implements PlayerType {
	playerID: string = '';
	nickname?: string | undefined;
	avatar?: string | undefined;
	isMe?: boolean | undefined;
	status?: 'AWAKE' | 'SLEEPING' | 'DEAD' | undefined;
	role?: 'WEREWOLF' | 'VILLAGER' | undefined;
	tags: { [key: string]: any; name: string }[] = [];
	onClick?: (player: RenderPlayer) => void;

	constructor(player: PlayerType) {
		Object.assign(this, player);
	}

	hasTag(pattern: { name: string; [key: string]: any }) {
		for (let tag of this.tags) {
			if (tag.name === pattern.name) return true;
		}
		return false;
	}
}

type PropsType = {
	player: RenderPlayer;
};

const Player = (props: PropsType) => {
	const player = props.player;
	return (
		<div
			className={`Player ${player.onClick ? 'onclick' : ''}`}
			onClick={
				player.onClick &&
				(() => {
					player.onClick?.(player);
				})
			}
		>
			<h1>{player.nickname}</h1>
			<h2>Status: {player.status || 'AWAKE'}</h2>
			<h2>Role: {player.role || 'UNKNOWN'}</h2>
			<ul>
				{player.tags.map((tag) => {
					return <li key={tag.name}>{tag.name}</li>;
				})}
			</ul>
		</div>
	);
};

export default Player;
export type { PlayerType };
export { RenderPlayer };

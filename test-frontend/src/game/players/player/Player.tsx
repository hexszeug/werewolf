import './Player.scss';

type PlayerType = {
	playerID: string;
	nickname?: string;
	avatar?: string;
	isMe?: boolean;
	status?: 'AWAKE' | 'SLEEPING' | 'DEAD';
	role?: 'WEREWOLF' | 'VILLAGER';
	tags: {
		name: string;
		[key: string]: any;
	}[];
};

type PropsType = {
	player: PlayerType;
	onClick: () => void;
};

const Player = (props: PropsType) => {
	const player = props.player;
	return (
		<div
			className='Player'
			onClick={props.onClick}
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

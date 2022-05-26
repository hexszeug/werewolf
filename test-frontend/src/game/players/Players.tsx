import './Players.scss';
import Player, { PlayerType } from './player/Player';

type PlayerListType = {
	[key: string]: PlayerType;
};

type PropsType = {
	players: PlayerListType;
	onClick?: (player: PlayerType) => void;
};

const Players = (props: PropsType) => {
	const players = Object.values(props.players);
	return (
		<div className='Players'>
			{players.map((player) => (
				<Player
					key={player.playerID}
					player={player}
					onClick={
						props.onClick
							? () => {
									props.onClick?.(player);
							  }
							: undefined
					}
				/>
			))}
		</div>
	);
};

export default Players;
export type { PlayerListType };

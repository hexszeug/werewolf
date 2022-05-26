import './Statusbar.scss';
import { PlayerType } from '../players/player/Player';

export const Statusbar = (props: { player: PlayerType }) => {
	return (
		<div className='Statusbar'>
			<h1>{props.player.nickname}</h1>
			<h1>{props.player.role}</h1>
			<h1>{props.player.status}</h1>
		</div>
	);
};

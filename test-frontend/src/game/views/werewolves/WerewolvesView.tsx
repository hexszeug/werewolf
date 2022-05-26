import Players, { PlayerListType } from '../../players/Players';

type PropsType = {
	players: PlayerListType;
	send: (type: string, data: unknown) => void;
};

const WerewolfvesView = (props: PropsType) => {
	return (
		<Players
			onClick={(player) => {
				props.send('werewolf/point', player.playerID);
			}}
			players={props.players}
		></Players>
	);
};

export default WerewolfvesView;

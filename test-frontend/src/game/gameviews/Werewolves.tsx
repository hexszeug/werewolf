import Players, { PlayerList } from '../players/Players';

type PropsType = {
	players: PlayerList;
	send: (type: string, data: unknown) => void;
};

export const WerewolfvesView = (props: PropsType) => {
	return (
		<Players
			onClick={(player) => {
				props.send('werewolf/point', player.playerID);
			}}
			players={props.players}
		></Players>
	);
};

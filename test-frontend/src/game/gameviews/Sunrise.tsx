import Players, { PlayerList } from '../players/Players';

export const SunriseView = (props: { players: PlayerList }) => {
	return <Players players={props.players} />;
};

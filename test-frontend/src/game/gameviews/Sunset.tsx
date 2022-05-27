import Players, { PlayerList } from '../players/Players';

export const SunsetView = (props: { players: PlayerList }) => {
	return <Players players={props.players} />;
};

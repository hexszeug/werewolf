import Players, { PlayerListType } from '../players/Players';

export const SunsetView = (props: { players: PlayerListType }) => {
	return <Players players={props.players} />;
};

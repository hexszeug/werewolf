import Players, { PlayerListType } from '../players/Players';

export const SunriseView = (props: { players: PlayerListType }) => {
	return <Players players={props.players} />;
};

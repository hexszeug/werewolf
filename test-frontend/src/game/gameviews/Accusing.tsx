import Players, { PlayerListType } from '../players/Players';

export const AccusingView = (props: { players: PlayerListType }) => {
	return <Players players={props.players} />;
};

import Players, { PlayerListType } from '../players/Players';

export const GameStartView = (props: { players: PlayerListType }) => {
	return <Players players={props.players} />;
};

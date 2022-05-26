import Players, { PlayerListType } from '../players/Players';

export const ExecutionView = (props: { players: PlayerListType }) => {
	return <Players players={props.players} />;
};
